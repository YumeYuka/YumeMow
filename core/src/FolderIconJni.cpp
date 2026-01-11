#include "FolderIconSetter.hpp"
#include "FileDialog.hpp"
#include <QString>

#ifdef ENABLE_JNI
#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL
Java_plus_yumeyuka_yumemow_FolderIconJni_nativeSelectFolder(JNIEnv* env, jobject) {
    const auto result = FileDialog::selectFolder();
    if (result.cancelled || result.path.isEmpty()) {
        return nullptr;
    }
    return env->NewStringUTF(result.path.toUtf8().constData());
}

JNIEXPORT jstring JNICALL
Java_plus_yumeyuka_yumemow_FolderIconJni_nativeOpenFile(JNIEnv* env, jobject, jstring filter) {
    QString filterStr;
    if (filter) {
        const char* filterChars = env->GetStringUTFChars(filter, nullptr);
        if (filterChars) {
            filterStr = QString::fromUtf8(filterChars);
            env->ReleaseStringUTFChars(filter, filterChars);
        }
    }

    const auto result = FileDialog::openFile(filterStr);
    if (result.cancelled || result.path.isEmpty()) {
        return nullptr;
    }
    return env->NewStringUTF(result.path.toUtf8().constData());
}

JNIEXPORT jint JNICALL
Java_plus_yumeyuka_yumemow_FolderIconJni_nativeSetFolderIcon(JNIEnv* env, jobject,
                                                              jstring folderPath,
                                                              jstring iconPath,
                                                              jint iconIndex) {
    if (!folderPath || !iconPath) {
        return static_cast<jint>(FolderIconError::InvalidPath);
    }

    const char* folderChars = env->GetStringUTFChars(folderPath, nullptr);
    const char* iconChars = env->GetStringUTFChars(iconPath, nullptr);

    if (!folderChars || !iconChars) {
        if (folderChars) env->ReleaseStringUTFChars(folderPath, folderChars);
        if (iconChars) env->ReleaseStringUTFChars(iconPath, iconChars);
        return static_cast<jint>(FolderIconError::InvalidPath);
    }

    const auto result = FolderIconSetter::setIcon(
        QString::fromUtf8(folderChars),
        QString::fromUtf8(iconChars),
        iconIndex
    );

    env->ReleaseStringUTFChars(folderPath, folderChars);
    env->ReleaseStringUTFChars(iconPath, iconChars);

    return static_cast<jint>(result.code);
}

#ifdef __cplusplus
}
#endif

#endif

