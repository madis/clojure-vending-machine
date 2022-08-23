(ns is.mad.vending.model
  (:require [clojure.spec.alpha :as s]))

; User
(def min-username-length 3)
(def min-password-length 6)

(s/def ::username (s/and string? #(> (count %) min-username-length)))
(s/def ::password (s/and string? #(> (count %) min-password-length)))
(s/def ::role #{"seller" "buyer"})

(s/def ::create-user-params (s/keys :req-un [::username ::password ::role]))

(defn valid-user? [user]
  (s/valid? ::create-user-params user))

; Product
(def max-slot-count 20)
(def max-quantity 20)
(defn below? [hi-limit value] (and (<= value hi-limit) (>= value 0)))

(s/def ::slot (s/and int? (partial below? max-slot-count)))
(s/def ::code int?)
(s/def ::quantity (s/and int? (partial below? max-quantity)))
(s/def ::size int?)
(s/def ::name string?)
(s/def ::price int?)

(s/def ::product-params (s/keys :req-un [::slot ::code ::quantity ::size ::name ::price]))

(defn valid-product? [product] (s/valid? ::product-params product))
