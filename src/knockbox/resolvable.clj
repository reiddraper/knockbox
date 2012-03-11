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

(ns knockbox.resolvable
  (:refer-clojure :exclude [resolve]))

(defprotocol Resolvable
    "Represents a type that can be treated
    as a CRDT (commutative replicated data type)."
    (resolve [a b])
    (gc [this gc-max-seconds gc-max-items]
        "Return a garbage-collected version of
        `this`. `gc-max-seconds` is the max time
        in seconds that garbage will be kept.
        `gc-max-items` is the number of garbage
        items that will be retained. Items are
        collected if they meet _either_ of these
        criteria. `nil` can be used for either
        parameter, meaning 'infinite', or
        'forever'"))
