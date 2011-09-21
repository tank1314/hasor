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
package org.more.services.remote;
import java.rmi.Remote;
/**
 * RMI���������ӿڣ�ͨ���ýӿڿ��Դ���RMI����
 * @version : 2011-8-16
 * @author ������ (zyc@byshell.org)
 */
public interface RmiBeanCreater {
    /**�������RMI����*/
    public Remote create() throws Throwable;
    /**��ȡ��RMI��������ṩ�ķ���ӿڡ�*/
    public Class<?>[] getFaces() throws Throwable;
}