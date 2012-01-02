(ns knockbox.test.sets
  (:require [knockbox.sets])
  (:use midje.sweet)
  (:use clojure.test))

(fact
  (let [s (knockbox.sets/observed-remove)]
    (contains? s :foo) => false))

(fact
  (let [s (knockbox.sets/observed-remove)
        f (conj s :foo)]
    (contains? f :foo) => true))

(fact
  (let [s (knockbox.sets/observed-remove)
        f (conj s :foo)]
    (contains? f :foo) => true))

(tabular
  (fact "sets contain items properly"
        (let [obs-rem (knockbox.sets/observed-remove)
              lww (knockbox.sets/lww)
              two-phase (knockbox.sets/two-phase)]
          (contains? ?set ?item) => ?expected))
        ?set                  ?item   ?expected
        obs-rem               :foo    false
        lww                   :foo    false
        two-phase             :foo    false

        (conj obs-rem :foo)   :foo    true
        (conj lww :foo)       :foo    true
        (conj two-phase :foo) :foo    true


        (-> obs-rem   (conj :foo) (disj :foo))    :foo false
        (-> lww       (conj :foo) (disj :foo))    :foo false
        (-> two-phase (conj :foo) (disj :foo))    :foo false

        (-> obs-rem   (conj :foo) (disj :bar))    :foo true
        (-> lww       (conj :foo) (disj :bar))    :foo true
        (-> two-phase (conj :foo) (disj :bar))    :foo true)


