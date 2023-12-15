package com.wtu.parking.utils;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page <T>{
    int pageSize;
    List<T> page;

    int curPage;

    int total;
}
