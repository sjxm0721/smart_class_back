package com.sjxm.springbootinit.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


/**
 * @Author: 四季夏目
 * @Date: 2024/12/2
 * @Description:Excel数据导入工具类
 */
@Slf4j
public class ExcelImportUtil {

    /**
     * 每次处理的数据量
     */
    private static final int BATCH_COUNT = 500;

    /**
     * 通用的Excel导入方法（支持额外参数）
     */
    public static <T, P> void importExcel(InputStream inputStream, Class<T> clazz, P param, BiConsumer<List<T>, P> consumer) {
        EasyExcel.read(inputStream, clazz, new ImportExcelListener<>(consumer, BATCH_COUNT, param)).sheet().doRead();
    }

    /**
     * Excel导入监听器
     *
     * @param <T> 泛型类型
     */

    private static class ImportExcelListener<T, P> extends AnalysisEventListener<T> {
        private final BiConsumer<List<T>, P> consumer;
        private final int batchCount;
        private final P param;
        private List<T> dataList;

        public ImportExcelListener(BiConsumer<List<T>, P> consumer, int batchCount, P param) {
            this.consumer = consumer;
            this.batchCount = batchCount;
            this.param = param;
            this.dataList = new ArrayList<>(batchCount);
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public void invoke(T data, AnalysisContext context) {
            dataList.add(data);
            if (dataList.size() >= batchCount) {
                consumer.accept(dataList, param);
                dataList = new ArrayList<>(batchCount);
            }
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public void doAfterAllAnalysed(AnalysisContext context) {
            if (!CollectionUtils.isEmpty(dataList)) {
                consumer.accept(dataList, param);
            }
        }
    }

}



