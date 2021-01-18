package com.tansun.risk.config;

import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DocumentFormatRegistry;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>处理PDF格式问题的注册器</p>
 *
 * @author : Pacee1
 * @date : 2021-01-13 15:42
 **/
//@Configuration
public class JodConverterConfiguration {
    //@Bean
    DocumentConverter localDocumentConverter(OfficeManager localOfficeManager, DocumentFormatRegistry documentFormatRegistry) {
        return LocalConverter.builder().filterChain(
//                new PageMarginsFilter(0,0,0,0), // 对word有用，多excel似乎没什么用
                new JodConverterRefreshFilter(true)
        ).officeManager(localOfficeManager).formatRegistry(documentFormatRegistry).build();
    }
}
