package com.group5.sebmmodels.bo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author deshperaydon
 * Manager class representing a user with managerial privileges.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Manager extends User{
    private Boolean isDelete;
}
