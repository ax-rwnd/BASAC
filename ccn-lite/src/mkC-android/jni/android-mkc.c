/* The BASAC port of ccn-lite-mkC for android.
 * TODO: License
 */

/*
 * @f util/ccn-lite-mkC.c
 * @b CLI mkContent, write to Stdout
 *
 * Copyright (C) 2013-15, Christian Tschudin, University of Basel
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 * File history:
 * 2013-07-06  created
 */

#define USE_SUITE_CCNB
#define USE_SUITE_CCNTLV
#define USE_SUITE_CISTLV
#define USE_SUITE_IOTTLV
#define USE_SUITE_NDNTLV
#define USE_HMAC256
#define USE_SIGNATURES

#define NEEDS_PACKET_CRAFTING

#include <jni.h>

//for mkc
#include "../../util/ccnl-common.c"
#include "../../util/ccnl-crypto.c"

#include <string.h>

char *private_key_path;
char *witness;

JNIEXPORT jint JNICALL Java_com_basac_androidmkc_AndroidmkC_generateContent(JNIEnv *env, jobject jthis, jstring content, jstring ipath, jstring opath) {
    const char *ncontent = (*env)->GetStringUTFChars(env, content, 0);
    const char *npath = (*env)->GetStringUTFChars(env, opath, 0);
    const char *nipath = (*env)->GetStringUTFChars(env, ipath, 0);

    char *hwstr = malloc(strlen(ncontent));
    memcpy(hwstr, ncontent, strlen(ncontent));


    unsigned char body[64*1024];
    unsigned char out[65*1024];
    unsigned char *publisher = out;
    const char *infname = nipath;//0;
    const char *outfname = npath;
    unsigned int chunknum = UINT_MAX, lastchunknum = UINT_MAX;
    int f, len, opt, plen, offs = 0;
    struct ccnl_prefix_s *name;
    int suite = CCNL_SUITE_DEFAULT;
    struct key_s *keys = NULL;

	//reads an input file to *out
    if (infname) {
        f = open(infname, O_RDONLY);
        if (f < 0)
            perror("file open:");
    } else
        f = 0;
    len = read(f, body, sizeof(body));
    close(f);
    memset(out, 0, sizeof(out));
    //char *argv[1] = {"hello world"};
    name = ccnl_URItoPrefix(hwstr, suite, NULL, chunknum == UINT_MAX ? NULL : &chunknum);
    //name = ccnl_URItoPrefix(argv[optind], suite, argv[optind+1],
    //                           chunknum == UINT_MAX ? NULL : &chunknum);

    switch (suite) {
    
#ifdef USE_SUITE_CCNB
    case CCNL_SUITE_CCNB:
        len = ccnl_ccnb_fillContent(name, body, len, NULL, out);
        break;
#endif
#ifdef USE_SUITE_CCNTLV
    case CCNL_SUITE_CCNTLV:

        offs = CCNL_MAX_PACKET_SIZE;
        if (keys) {
            unsigned char keyval[64];
            unsigned char keyid[32];
            // use the first key found in the key file
            ccnl_hmac256_keyval(keys->key, keys->keylen, keyval);
            ccnl_hmac256_keyid(keys->key, keys->keylen, keyid);
            len = ccnl_ccntlv_prependSignedContentWithHdr(name, body, len,
                  lastchunknum == UINT_MAX ? NULL : &lastchunknum,
                  NULL, keyval, keyid, &offs, out);
        } else
            len = ccnl_ccntlv_prependContentWithHdr(name, body, len,
                          lastchunknum == UINT_MAX ? NULL : &lastchunknum,
                          NULL  /*Int *contentpos */, &offs, out);
        break;
#endif
#ifdef USE_SUITE_CISTLV
    case CCNL_SUITE_CISTLV:
        offs = CCNL_MAX_PACKET_SIZE;
        len = ccnl_cistlv_prependContentWithHdr(name, body, len,
                  lastchunknum == UINT_MAX ? NULL : &lastchunknum,
                  NULL, &offs, out);
        break;
#endif
#ifdef USE_SUITE_IOTTLV
    case CCNL_SUITE_IOTTLV:
        offs = CCNL_MAX_PACKET_SIZE;
        if (ccnl_iottlv_prependReply(name, body, len, &offs, NULL,
                   lastchunknum == UINT_MAX ? NULL : &lastchunknum, out) < 0
              || ccnl_switch_prependCoding(CCNL_ENC_IOT2014, &offs, out) < 0)
            return -1;
        len = CCNL_MAX_PACKET_SIZE - offs;
        break;
#endif

#ifdef USE_SUITE_NDNTLV
    case CCNL_SUITE_NDNTLV:
        offs = CCNL_MAX_PACKET_SIZE;
        if (keys) {
            unsigned char keyval[64];
            unsigned char keyid[32];
            // use the first key found in the key file
            ccnl_hmac256_keyval(keys->key, keys->keylen, keyval);
            ccnl_hmac256_keyid(keys->key, keys->keylen, keyid);
            len = ccnl_ndntlv_prependSignedContent(name, body, len,
                  lastchunknum == UINT_MAX ? NULL : &lastchunknum,
                  NULL, keyval, keyid, &offs, out);
        } else {
            len = ccnl_ndntlv_prependContent(name, body, len,
                  NULL, lastchunknum == UINT_MAX ? NULL : &lastchunknum,
                  &offs, out);
        }
        break;
#endif
    default:
        break;
    }

    if (outfname) {
        f = creat(outfname, 0666);
        if (f < 0)
            perror("file open:");
    } else
        f = 1;
    write(f, out + offs, len);
    close(f);
    return 0;
}

