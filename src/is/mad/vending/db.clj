(ns is.mad.vending.db)

(def db (atom {}))

(defn clean-db! [] (reset! db {}))

(defn update-db [key-path new-value]
  (reset! db (assoc-in @db key-path new-value)))

(defn fetch [key-path]
  (get-in @db key-path))

(defn exists? [key-path]
  (not (nil? (get-in @db key-path))))
