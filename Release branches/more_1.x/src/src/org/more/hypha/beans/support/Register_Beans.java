/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.more.hypha.beans.support;
import org.more.core.xml.XmlParserKit;
import org.more.hypha.configuration.NameSpaceRegister;
import org.more.hypha.configuration.XmlConfiguration;
/**
 * ����ʵ����{@link NameSpaceRegister}�ӿڲ����ṩ�˶������ռ䡰http://project.byshell.org/more/schema/beans���Ľ���֧�֡�
 * @version 2010-9-15
 * @author ������ (zyc@byshell.org)
 */
public class Register_Beans implements NameSpaceRegister {
    /**�ṩ֧�ֵ������ռ�*/
    public static final String DefaultNameSpaceURL = "http://project.byshell.org/more/schema/beans";
    /**ִ�г�ʼ��ע�ᡣ*/
    public void initRegister(String namespaceURL, XmlConfiguration config) {
        //1.ע���ǩ������
        XmlParserKit kit = new XmlParserKit();
        kit.regeditHook("/beans", new TagBeans_Beans(config));
        kit.regeditHook("/beans/meta", new TagBeans_MetaData(config));
        kit.regeditHook("/beans/defaultPackage", new TagBeans_DefaultPackage(config));
        kit.regeditHook(new String[] { "/beans/package", "/beans/*/package" }, new TagBeans_Package(config));
        //
        kit.regeditHook(new String[] { "/beans/classBean", "/beans/*/classBean" }, new TagBeans_ClassBean(config));
        kit.regeditHook(new String[] { "/beans/generateBean", "/beans/*/generateBean" }, new TagBeans_GenerateBean(config));
        kit.regeditHook(new String[] { "/beans/refBean", "/beans/*/refBean" }, new TagBeans_RefBean(config));
        kit.regeditHook(new String[] { "/beans/scriptBean", "/beans/*/scriptBean" }, new TagBeans_ScriptBean(config));//ͬʱ�߱�text��element
        kit.regeditHook(new String[] { "/beans/templateBean", "/beans/*/templateBean" }, new TagBeans_TemplateBean(config));
        kit.regeditHook(new String[] { "/beans/varBean", "/beans/*/varBean" }, new TagBeans_VarBean(config));
        //
        kit.regeditHook("/beans/*Bean/constructor-arg", new TagBeans_Constructor(config));
        kit.regeditHook("/beans/*Bean/property", new TagBeans_Property(config));
        kit.regeditHook("/beans/*Bean/meta", new TagBeans_MetaData(config));
        kit.regeditHook("/beans/*Bean/*/meta", new TagBeans_MetaData(config));
        kit.regeditHook("/beans/*Bean/method", new TagBeans_Method(config));
        kit.regeditHook("/beans/*Bean/method/param", new TagBeans_Param(config));
        //
        kit.regeditHook("/beans/*Bean/*/value", new TagBeans_Value(config));
        kit.regeditHook("/beans/*Bean/*/date", new TagBeans_Date(config));
        kit.regeditHook("/beans/*Bean/*/enum", new TagBeans_Enum(config));
        kit.regeditHook("/beans/*Bean/*/bigText", new TagBeans_BigText(config));
        kit.regeditHook("/beans/*Bean/*/ref", new TagBeans_Ref(config));
        kit.regeditHook("/beans/*Bean/*/file", new TagBeans_File(config));
        kit.regeditHook("/beans/*Bean/*/directory", new TagBeans_Directory(config));
        kit.regeditHook("/beans/*Bean/*/uri", new TagBeans_URI(config));
        kit.regeditHook("/beans/*Bean/*/array", new TagBeans_Array(config));
        kit.regeditHook("/beans/*Bean/*/set", new TagBeans_Set(config));
        kit.regeditHook("/beans/*Bean/*/list", new TagBeans_List(config));
        kit.regeditHook("/beans/*Bean/*/map", new TagBeans_Map(config));
        kit.regeditHook("/beans/*Bean/*/map/entity", new TagBeans_Entity(config));
        //
        //2.ע�������ռ�
        if (namespaceURL == null)
            namespaceURL = DefaultNameSpaceURL;
        config.regeditXmlParserKit(namespaceURL, kit);
        //3.ע���������ֵ��������˳��������ȼ���
        /*��xml����ͼ�����˶����������ֵʱ�����ȼ����������ã�����ͬʱ������value �� refBean���ԡ���ôvalue�����ȼ���refBean�ߡ�*/
        config.regeditQuickParser(new QPP_Value());
        config.regeditQuickParser(new QPP_Date());
        config.regeditQuickParser(new QPP_Enum());
        config.regeditQuickParser(new QPP_Ref());
        config.regeditQuickParser(new QPP_File());
        config.regeditQuickParser(new QPP_Directory());
        config.regeditQuickParser(new QPP_URILocation());
    }
}