package com.yoavs.eventer.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yoavs
 */

public class Request implements Serializable {

    private String key;
    private String itemName;
    private Long lastUpdate;
    private Boolean purchase;
    private String approvalUserId;
    private String suggestedUserId;

    public Request() {
        // Default constructor required for calls to DataSnapshot.getValue(Request.class)
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Boolean getPurchase() {
        return purchase;
    }

    public void setPurchase(Boolean purchase) {
        this.purchase = purchase;
    }

    public String getApprovalUserId() {
        return approvalUserId;
    }

    public void setApprovalUserId(String approvalUserId) {
        this.approvalUserId = approvalUserId;
    }

    public String getSuggestedUserId() {
        return suggestedUserId;
    }

    public void setSuggestedUserId(String suggestedUserId) {
        this.suggestedUserId = suggestedUserId;
    }

    public Map<String, Object> getValues() {
        Map<String, Object> values = new HashMap<>();

        values.put("suggestedUserId", getSuggestedUserId());
        values.put("itemName", getItemName());
        values.put("lastUpdate", getLastUpdate());
        values.put("purchase", getPurchase());
        values.put("approvalUserId", getApprovalUserId());

        return values;
    }
}
