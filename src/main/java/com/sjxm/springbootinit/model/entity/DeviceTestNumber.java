package com.sjxm.springbootinit.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceTestNumber implements Serializable {

    private Integer deviceId;

    private Integer deviceUsedNum;
}