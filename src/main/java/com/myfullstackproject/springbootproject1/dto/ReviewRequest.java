package com.myfullstackproject.springbootproject1.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer stars;
    
    @NotBlank(message = "Comment is required")
    @Size(max = 500, message = "Comment must not exceed 500 characters")
    private String comment;
}
