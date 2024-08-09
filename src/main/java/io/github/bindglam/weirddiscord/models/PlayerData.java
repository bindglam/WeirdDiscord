package io.github.bindglam.weirddiscord.models;

import java.util.UUID;

public class PlayerData {
    private UUID uuid;
    private String id;

    public PlayerData(UUID uuid, String id) {
        this.uuid = uuid;
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
