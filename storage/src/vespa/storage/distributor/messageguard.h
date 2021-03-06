// Copyright 2017 Yahoo Holdings. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
#pragma once

#include "pendingclusterstate.h"
#include <vespa/storage/common/messagesender.h>
#include <vespa/vespalib/util/sync.h>

namespace storage {

class MessageGuard {
    std::vector<std::shared_ptr<api::StorageMessage> > messagesUp;
    std::vector<std::shared_ptr<api::StorageMessage> > messagesDown;

    vespalib::LockGuard _lock;
    ChainedMessageSender& _messageSender;

public:
    MessageGuard(const vespalib::Lock &lock,
                 ChainedMessageSender& messageSender)
        : _lock(lock),
          _messageSender(messageSender) {}

    void send(const std::shared_ptr<api::StorageMessage>& message) {
        sendUp(message);
    }

    void sendUp(const std::shared_ptr<api::StorageMessage>& message) {
        messagesUp.push_back(message);
    }

    void sendDown(const std::shared_ptr<api::StorageMessage>& message) {
        messagesDown.push_back(message);
    }

    ~MessageGuard() {
        _lock.unlock();
        for (uint32_t i = 0; i < messagesUp.size(); i++) {
            _messageSender.sendUp(messagesUp[i]);
        }
        for (uint32_t i = 0; i < messagesDown.size(); i++) {
            _messageSender.sendDown(messagesDown[i]);
        }
    }
};

}

