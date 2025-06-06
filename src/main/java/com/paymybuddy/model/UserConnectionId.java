package com.paymybuddy.model;

import java.io.Serializable;
import java.util.Objects;

public class UserConnectionId implements Serializable {
    private Integer user;
    private Integer connection;

    public UserConnectionId() {}

    public UserConnectionId(Integer user, Integer connection) {
        this.user = user;
        this.connection = connection;
    }

    // equals() et hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserConnectionId that = (UserConnectionId) o;
        return Objects.equals(user, that.user) && Objects.equals(connection, that.connection);
    }
    @Override
    public int hashCode() {
        return Objects.hash(user, connection);
    }
}
