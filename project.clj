;; -------------------------------------------------------------------
;; Copyright (c) 2011 Basho Technologies, Inc.  All Rights Reserved.
;;
;; This file is provided to you under the Apache License,
;; Version 2.0 (the "License"); you may not use this file
;; except in compliance with the License.  You may obtain
;; a copy of the License at
;;
;;   http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing,
;; software distributed under the License is distributed on an
;; "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
;; KIND, either express or implied.  See the License for the
;; specific language governing permissions and limitations
;; under the License.
;;
;; -------------------------------------------------------------------

(defproject knockbox "0.0.1-SNAPSHOT"
  :description "An eventual-consistency toolbox for Clojure, and eventually the JVM in general."
  :dependencies [[org.clojure/clojure "1.3.0"]]
  :dev-dependencies [[midje "1.3.1"  :exclusions [org.clojure/clojure]]
                     [lein-midje "1.0.7"]])
