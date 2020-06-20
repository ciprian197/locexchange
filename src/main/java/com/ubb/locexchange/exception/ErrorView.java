package com.ubb.locexchange.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ubb.locexchange.exception.error.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
class ErrorView {

    private ErrorType error;
    private String message;

}
