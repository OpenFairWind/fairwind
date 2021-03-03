package it.uniparthenope.fairwind.sdk.model;

import mjson.Json;

/**
 * Created by raffaelemontella on 02/06/16.
 */
public interface Preferences {
    public String getConfigProperty(String keyString);
    public String getConfigProperty(String keyString, String defValue);
    public Integer getConfigPropertyInt(String keyString);
    public Integer getConfigPropertyInt(String keyString, Integer defValue);
    public Boolean getConfigPropertyBoolean(String keyString);
    public Boolean getConfigPropertyBoolean(String keyString, Boolean defValue);

    public Json getConfigPropertyJson(String keyString);
    public Json getConfigPropertyJson(String keyString, Json defValue);

    public void setConfigProperty(int keyStringId, String value);
    public void setConfigProperty(String key, String value);
    public void setConfigPropertyInt(String key, int value);
    public void setConfigPropertyBoolean(String key, boolean value);

    public void setConfigPropertyJson(String key, Json json);
}
