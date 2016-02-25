# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH := $(call my-dir)
MY_PATH := $(LOCAL_PATH)
#$(warning "$(LOCAL_PATH)/openssl-1.0.2/armeabi-v7a/lib/libssl.a" )
include $(CLEAR_VARS)
LOCAL_PATH := $(MY_PATH)

LOCAL_MODULE := openssl
#LOCAL_SRC_FILES := ../openssl-1.0.2/armeabi-v7a/lib/libssl.a
LOCAL_SRC_FILES := libssl_1_0_0.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/openssl-1.0.2/include/
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := crypto 
#LOCAL_SRC_FILES := ../openssl-1.0.2/armeabi-v7a/lib/libcrypto.a
LOCAL_SRC_FILES := libcrypto_1_0_0.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/openssl-1.0.2/include/
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := androidmkc
LOCAL_SRC_FILES := android-mkc.c
LOCAL_SHARED_LIBRARIES := openssl crypto
LOCAL_LDLIBS    := #-landroid -L$(LOCAL_PATH)/../lib/
#LOCAL_C_INCLUDES := $(LOCAL_PATH)/openssl-1.0.2/

include $(BUILD_SHARED_LIBRARY)

