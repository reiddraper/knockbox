(ns knockbox.test.sets
  (:require knockbox.sets)
  (:require [knockbox.resolvable :as resolvable])
  (:require [knockbox.core :as kbc])
  (:use midje.sweet)
  (:use clojure.test))

(tabular
  (fact "sets contain items properly"
        (let [obs-rem   (knockbox.sets/observed-remove)
              lww       (knockbox.sets/lww)
              two-phase (knockbox.sets/two-phase)]
          (contains? ?set ?item) => ?expected))
        ?set                      ?item           ?expected
        obs-rem                   :foo            false
        lww                       :foo            false
        two-phase                 :foo            false

        (conj obs-rem :foo)       :foo            true
        (conj lww :foo)           :foo            true
        (conj two-phase :foo)     :foo            true


        (-> obs-rem   (conj :foo) (disj :foo))    :foo false
        (-> lww       (conj :foo) (disj :foo))    :foo false
        (-> two-phase (conj :foo) (disj :foo))    :foo false

        (-> obs-rem   (conj :foo) (disj :bar))    :foo true
        (-> lww       (conj :foo) (disj :bar))    :foo true
        (-> two-phase (conj :foo) (disj :bar))    :foo true)

(tabular
  (fact "sets resolve properly"
        (let [obs-rem   (knockbox.sets/observed-remove)
              lww       (knockbox.sets/lww)
              two-phase (knockbox.sets/two-phase)

              obr-a (into obs-rem #{:foo :bar :baz})
              lww-a (into lww #{:foo :bar :baz})
              tp-a  (into two-phase #{:foo :bar :baz})

              obr-b (disj obr-a :foo)
              lww-b (disj lww :foo)
              tp-b  (disj tp-a :foo)]
          (contains? ?set ?item) => ?expected))
        ?set                        ?item           ?expected
        (kbc/resolve [obr-a obr-b]) :foo            false
        (kbc/resolve [lww-a lww-b]) :foo            false
        (kbc/resolve [tp-a tp-b])   :foo            false

        ; args are commutative
        (kbc/resolve [obr-b obr-a]) :foo            false
        (kbc/resolve [lww-b lww-a]) :foo            false
        (kbc/resolve [tp-b tp-a])   :foo            false)

(tabular
  (fact "an encode-decode json/clojure-reader cycle is the same
        as the original"
        (let [obs-rem   (knockbox.sets/observed-remove)
              lww       (knockbox.sets/lww)
              two-phase (knockbox.sets/two-phase)

              lww-a (into lww #{:foo :bar :baz})
              lww-b (disj lww-a :foo)
              lww-c (disj lww :foo)

              obs-rem-a (into obs-rem #{:foo :bar :baz})
              obs-rem-b (disj obs-rem-a :foo)
              obs-rem-c (disj obs-rem :foo)

              two-phase-a (into two-phase #{:foo :bar :baz})
              two-phase-b (disj two-phase-a :foo)
              two-phase-c (disj two-phase :foo)]

          (knockbox.core/from-json (knockbox.core/to-json ?set)) => ?set
          (read-string (binding [*print-dup* true] (pr-str ?set))) => ?set))
          ?set
          lww
          lww-a
          lww-b
          lww-c

          obs-rem
          obs-rem-a
          obs-rem-b
          obs-rem-c

          two-phase
          two-phase-a
          two-phase-b
          two-phase-c)

(tabular
  (fact "sets equal themselves"
    (let [obs-rem   (knockbox.sets/observed-remove)
          lww       (knockbox.sets/lww)
          two-phase (knockbox.sets/two-phase)

          obr-a (into obs-rem #{:foo :bar :baz})
          lww-a (into lww #{:foo :bar :baz})
          tp-a  (into two-phase #{:foo :bar :baz})

          obr-b (disj obr-a :foo)
          lww-b (disj lww :foo)
          tp-b  (disj tp-a :foo)]
      ?set => ?set))
      ?set
      obs-rem
      lww
      two-phase
      obr-a
      lww-a
      tp-a
      obr-b
      lww-b
      tp-b)

(tabular
  (fact "GC with infinite memory equals no GC"
    (let [obs-rem   (knockbox.sets/observed-remove)
          lww       (knockbox.sets/lww)
          two-phase (knockbox.sets/two-phase)

          obr-a (into obs-rem #{:foo :bar :baz})
          lww-a (into lww #{:foo :bar :baz})
          tp-a  (into two-phase #{:foo :bar :baz})

          obr-b (disj obr-a :foo)
          lww-b (disj lww :foo)
          tp-b  (disj tp-a :foo)]
      (resolvable/gc ?set nil nil) => ?set))
      ?set
      obs-rem
      lww
      two-phase
      obr-a
      lww-a
      tp-a
      obr-b
      lww-b
      tp-b)
