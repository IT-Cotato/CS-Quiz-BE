package cotato.csquiz.domain.entity;

import lombok.Getter;

@Getter
public enum Networking {

    NW_ON("NETWORKING_ON"),
    NW_OFF("NETWORKING_OFF");

    private final String networking;

    Networking(String networking) {
        this.networking = networking;
    }
}
