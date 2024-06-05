/*
 * Copyright 2024-2024 Chris de Vreeze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package chapter12;

import javax.sql.rowset.RowSetFactory;
import javax.transaction.xa.XAResource;
import javax.xml.parsers.SAXParserFactory;
import java.lang.module.ModuleDescriptor;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.logging.LogManager;

/**
 * Example programming showing JDK module information, using a standard Java API for this module introspection.
 * <p>
 * We can get the same information with "java --describe-module java.sql" etc. on the command line.
 *
 * @author Chris de Vreeze
 */
public class JdkModuleInspectionExample {

    private static void printModuleInformation(ModuleDescriptor moduleDescriptor) {
        System.out.printf("Module name: %s%n", moduleDescriptor.name());
        System.out.printf("Is automatic: %s%n", moduleDescriptor.isAutomatic());
        System.out.printf("Is open: %s%n", moduleDescriptor.isOpen());

        moduleDescriptor.version().ifPresent(version -> System.out.printf("Version: %s%n", version));
        moduleDescriptor.mainClass().ifPresent(cls -> System.out.printf("Main class: %s%n", cls));

        moduleDescriptor.modifiers().stream().sorted().forEach(mod -> System.out.printf("Modifier: %s%n", mod.name()));

        moduleDescriptor.exports().stream().sorted().forEach(exp -> System.out.printf("Exports: %s%n", exp));

        moduleDescriptor.requires().stream().sorted().forEach(req -> System.out.printf("Requires: %s%n", req));

        moduleDescriptor.opens().stream().sorted().forEach(op -> System.out.printf("Opens: %s%n", op));

        moduleDescriptor.provides().stream().sorted().forEach(prov -> System.out.printf("Provides: %s%n", prov));

        moduleDescriptor.uses().stream().sorted().forEach(us -> System.out.printf("Uses: %s%n", us));

        moduleDescriptor.packages().stream().sorted().forEach(pkg -> System.out.printf("Contains package: %s%n", pkg));
    }

    public static void main(String[] args) {
        ModuleDescriptor baseModuleDescriptor =
                Optional.ofNullable(List.class.getModule()).flatMap(m -> Optional.ofNullable(m.getDescriptor())).orElseThrow();

        ModuleDescriptor xmlModuleDescriptor =
                Optional.ofNullable(SAXParserFactory.class.getModule()).flatMap(m -> Optional.ofNullable(m.getDescriptor())).orElseThrow();

        ModuleDescriptor xaModuleDescriptor =
                Optional.ofNullable(XAResource.class.getModule()).flatMap(m -> Optional.ofNullable(m.getDescriptor())).orElseThrow();

        ModuleDescriptor loggingModuleDescriptor =
                Optional.ofNullable(LogManager.class.getModule()).flatMap(m -> Optional.ofNullable(m.getDescriptor())).orElseThrow();

        ModuleDescriptor sqlModuleDescriptor =
                Optional.ofNullable(PreparedStatement.class.getModule()).flatMap(m -> Optional.ofNullable(m.getDescriptor())).orElseThrow();

        ModuleDescriptor rowsetModuleDescriptor =
                Optional.ofNullable(RowSetFactory.class.getModule()).flatMap(m -> Optional.ofNullable(m.getDescriptor())).orElseThrow();

        System.out.println();
        printModuleInformation(baseModuleDescriptor);

        System.out.println();
        printModuleInformation(xmlModuleDescriptor);

        System.out.println();
        printModuleInformation(xaModuleDescriptor);

        System.out.println();
        printModuleInformation(loggingModuleDescriptor);

        System.out.println();
        printModuleInformation(sqlModuleDescriptor);

        System.out.println();
        printModuleInformation(rowsetModuleDescriptor);
    }
}
