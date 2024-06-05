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

import java.util.Optional;
import java.util.ServiceLoader;

/**
 * Example that pretends to have separate Java modules for a service API, its locator, its use and its implementation.
 *
 * @author Chris de Vreeze
 */
public class ServiceLocatorExample {

    public static class ServiceApiWannabeModule {

        public record Quote(String attributedTo, String text) {
        }

        public interface QuoteService {
            Quote findQuoteOfTheDay(); // recall that "public" and "abstract" are implied
        }
    }

    public static class ServiceLocatorWannabeModule {

        public static class QuoteServiceFinder {

            public static ServiceApiWannabeModule.QuoteService findQuoteService() {
                ServiceLoader<ServiceApiWannabeModule.QuoteService> loader =
                        ServiceLoader.load(ServiceApiWannabeModule.QuoteService.class);

                for (ServiceApiWannabeModule.QuoteService service : loader) {
                    return service;
                }
                return null;
            }
        }
    }

    public static class ServiceUseWannabeModule {

        public static class QuoteProgram {

            public static void main(String[] args) {
                // Service lookup does not work in this example, so we cheat and instantiate the service ourselves
                ServiceApiWannabeModule.QuoteService quoteService =
                        Optional.ofNullable(ServiceLocatorWannabeModule.QuoteServiceFinder.findQuoteService())
                                .orElse(new ServiceImplementationWannabeModule.QuoteServiceImpl());

                ServiceApiWannabeModule.Quote quoteOfTheDay = quoteService.findQuoteOfTheDay();

                System.out.printf("Quote of the day: %s%n", quoteOfTheDay);
            }
        }
    }

    public static class ServiceImplementationWannabeModule {

        public static class QuoteServiceImpl implements ServiceApiWannabeModule.QuoteService {

            @Override
            public ServiceApiWannabeModule.Quote findQuoteOfTheDay() {
                return new ServiceApiWannabeModule.Quote(
                        "Wim Hof",
                        "If you can learn how to use your mind, anything is possible."
                );
            }
        }
    }

    public static void main(String[] args) {
        ServiceUseWannabeModule.QuoteProgram.main(new String[0]);
    }
}
