package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.foundry.resource_calculator.util.ListUtil;
import org.joelson.mattias.foundry.resource_calculator.util.StringUtil;

import java.util.List;

record JsonMakerGroup(
        String groupName,
        List<String> makerNames) {

    @JsonCreator
    public JsonMakerGroup(
            @JsonProperty(value = "groupName", required = true) String groupName,
            @JsonProperty(value = "makerNames", required = true) List<String> makerNames) {
        this.groupName = StringUtil.requireNotNullAndNotEmpty(groupName, "groupName is null or empty");
        this.makerNames = ListUtil.requireUniqueMembers(makerNames);
    }
}
