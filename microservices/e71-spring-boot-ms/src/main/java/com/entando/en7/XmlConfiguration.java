/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.entando.en7;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author E.Santoboni
 */
@Configuration
@ImportResource({"classpath:spring/propertyPlaceholder.xml",
			"classpath:spring/msBaseSystemConfig.xml",
			"classpath*:spring/aps/**/**.xml",
			"classpath*:spring/plugins/**/aps/**/**.xml"})
public class XmlConfiguration {



}
