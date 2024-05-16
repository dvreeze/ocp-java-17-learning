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

package chapter06;

/**
 * Example showing object initialization order, in the presence of inheritance.
 *
 * @author Chris de Vreeze
 */
public class ObjectInitializationOrderWithInheritanceExample {

    // Non-instantiable
    private ObjectInitializationOrderWithInheritanceExample() {
    }

    public static abstract class Vehicle {

        private final String name;

        {
            System.out.printf("Class Vehicle instance initializer block (runtime class: %s)%n", this.getClass().getSimpleName());
        }

        public Vehicle(String name) {
            System.out.println("In Vehicle constructor");
            this.name = name;
            System.out.println("Leaving Vehicle constructor");
        }

        public String getName() {
            return name;
        }
    }

    public static abstract class RailedVehicle extends Vehicle {

        {
            System.out.printf("Class RailedVehicle instance initializer block (runtime class: %s)%n", this.getClass().getSimpleName());
        }

        {
            System.out.printf("Class RailedVehicle 2nd instance initializer block (runtime class: %s)%n", this.getClass().getSimpleName());
        }

        public RailedVehicle(String name) {
            super(name);
            System.out.println("In RailedVehicle constructor");
        }
    }

    public static class Train extends RailedVehicle {

        public Train(String name) {
            super(name);
            System.out.println("In Train constructor");
        }

        {
            System.out.printf("Class Train instance initializer block (runtime class: %s)%n", this.getClass().getSimpleName());
        }
    }

    public static class BulletTrain extends Train {

        {
            System.out.printf("Class BulletTrain instance initializer block (runtime class: %s)%n", this.getClass().getSimpleName());
        }

        public BulletTrain(String name) {
            super(name);
            System.out.println("In BulletTrain constructor");
        }
    }

    public static void main(String[] args) {
        Train train = new BulletTrain("Hayabusa");
        System.out.println("Train name: " + train.getName());
    }
}
