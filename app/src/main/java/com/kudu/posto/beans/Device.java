package com.kudu.posto.beans;

public class Device {

    private String registration_id;
    private String cloud_message_type;

    public String getCloud_message_type() {
        return cloud_message_type;
    }

    public void setCloud_message_type(String cloud_message_type) {
        this.cloud_message_type = cloud_message_type;
    }

    public String getRegistration_id() {
        return registration_id;
    }

    public void setRegistration_id(String registration_id) {
        this.registration_id = registration_id;
    }
}
