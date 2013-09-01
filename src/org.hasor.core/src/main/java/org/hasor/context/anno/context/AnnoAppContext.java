/*
 * Copyright 2008-2009 the original ������(zyc@hasor.net).
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
package org.hasor.context.anno.context;
import java.io.IOException;
import java.util.Set;
import org.hasor.Hasor;
import org.hasor.context.HasorModule;
import org.hasor.context.ModuleInfo;
import org.hasor.context.ModuleSettings;
import org.hasor.context.anno.DefineModule;
import org.hasor.context.core.DefaultAppContext;
import org.hasor.context.module.GuiceModulePropxy;
import org.more.util.StringUtils;
import com.google.inject.Module;
/**
 * 
 * @version : 2013-7-16
 * @author ������ (zyc@hasor.net)
 */
public class AnnoAppContext extends DefaultAppContext {
    public AnnoAppContext() throws IOException {
        super();
    }
    public AnnoAppContext(String mainConfig) throws IOException {
        super(mainConfig);
    }
    public AnnoAppContext(String mainConfig, Object context) throws IOException {
        super(mainConfig, context);
    }
    protected void initContext() throws IOException {
        super.initContext();
        this.loadModule();
    }
    //
    /**װ��ģ��*/
    protected void loadModule() {
        //1.ɨ��classpath��
        Set<Class<?>> initHookSet = this.getClassSet(DefineModule.class);
        Hasor.info("find Module : " + Hasor.logString(initHookSet));
        //2.����δʵ��HasorModule�ӿڵ���
        for (Class<?> modClass : initHookSet) {
            HasorModule modObject = null;
            ModuleInfo moduleInfo = null;
            if (HasorModule.class.isAssignableFrom(modClass) == true) {
                /*Hasor ģ��*/
                modObject = this.createModule(modClass);
                moduleInfo = this.addModule(modObject);
            } else if (Module.class.isAssignableFrom(modClass) == true) {
                /*Guice ģ��*/
                Module guiceObject = this.createModule(modClass);
                moduleInfo = this.addModule(new GuiceModulePropxy(guiceObject));
            } else
                /*����*/
                Hasor.warning("not implemented HasorModule or Module :%s", moduleInfo);
            //
            if (moduleInfo instanceof ModuleSettings) {
                ModuleSettings infoCfg = (ModuleSettings) moduleInfo;
                DefineModule modAnno = modClass.getAnnotation(DefineModule.class);
                String dispName = StringUtils.isBlank(modAnno.displayName()) ? moduleInfo.getModuleObject().getClass().getSimpleName() : modAnno.displayName();
                infoCfg.setDisplayName(dispName);
                infoCfg.setDescription(modAnno.description());
            }
        }
    }
    private <T> T createModule(Class<?> listenerClass) {
        try {
            return (T) listenerClass.newInstance();
        } catch (Exception e) {
            Hasor.error("create %s an error!%s", listenerClass, e);
            return null;
        }
    }
}