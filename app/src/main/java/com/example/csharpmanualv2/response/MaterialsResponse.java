package com.example.csharpmanualv2.response;

import com.example.csharpmanualv2.model.Material;

public class MaterialsResponse {
    public Material theory;
    public Material practice;

    public MaterialsResponse(Material theory, Material practice) {
        this.theory = theory;
        this.practice = practice;
    }
}
