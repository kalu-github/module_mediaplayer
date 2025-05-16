// app/src/main/cpp/logging.c
#include "logging.h"

// 默认关闭
int logLevel = 0;

void setLevel(jboolean flag) {
    if (flag) {
        logLevel = 1;
    } else {
        logLevel = 0;
    }
}

int getLevel() {
    return logLevel;
}