// app/src/main/cpp/logging.c
#include "logging.h"

// 默认开启
int logLevel = 1;

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