(ns quincy-the-game-1.core
  (:gen-class)
  (:import
   [java.util UUID])
  (:require
   [quincy-the-game-1.customer :as customer]
   [clj-stacktrace.core :as stack]
   [clojure.pprint :as pretty]
   [clojure.core.match :refer [match]]))



(def menu
  "All the items a customer at Quincy's can order. Customers with allergies should try to avoid anything with peanut-oil. In this version of the game, we don't use the ingredients. We will use them when we build the final version of the game."
  {:ham-and-bacon-omelete [:eggs :salt :pepper :ham :bacon :peanut-oil]
   :alfalfa-sprouts-and-dandelion-salad [:alfalfa-sprouts :dandelion-petals :herbs-and-peanut-oil-dressing :carrots :parsely]
   :deep-fried-dodo-bird [:dodo-bird :canola-oil :rice :carrots :parsely]
   :tofu-and-ginger [:tofu :ginger :oil]})

(defn different-order [order]
  {:pre [
         (map? order)
         (:waiter order)
         (:customer order)
         (:menu-item-name order)
         ]
   :post [
          (map? %)
          (:waiter %)
          (:customer %)
          (:menu-item-name %)
          ]}
  ;; if we wanted to be terse, we could write this as one line:
  ;; (apply vector (filter #(not (= (:menu-item-name order) %)) (keys menu)))
  ;; but terse code can be difficult to decipher when you come back to it months later

  (let [menu-items (keys menu)
        
        ;; a truncated menu -- this next line gives us a list of every key in menu,
        ;; except for the key of whatever the customer previously ordered
        menu-items (filter #(not (= (:menu-item-name order) %)) menu-items)

        ;; we have a list but we will want a vector when we call nth, so we convert
        menu-items (apply vector menu-items)

        how-many-menu-items (count menu-items)

        ;; since rand-int excludes the number it is given, this generates a
        ;; random number in a range from 0 to one less than the number of 
        ;; items in menu-items
        which-item (rand-int how-many-menu-items)

        ;; we fetch a random item from the truncated menu
        new-menu-item-name (nth menu-items which-item)]

    ;; we return the new order
    (assoc order :menu-item-name new-menu-item-name)))

(defn change-customer-order? []
  "In real life, we would not allow customers to change their minds this much."
  (if (= (rand-int 4) 0)
    true
    false))

(defn check-to-see-if-this-waiters-customers-are-happy []
  (doseq [o (customer/orders)]
    (when (change-customer-order?)
      (println "The customer is unhappy!")
      (println "We must change the customer's order!")
      (pretty/pprint o)
      (customer/change-customer-orders o (different-order o)))))

(defn customer-order [{:keys [allergies vegetarian _]}]
  (match [allergies vegetarian]
          [0 0] :ham-and-bacon-omelete
          [0 1] :alfalfa-sprouts-and-dandelion-salad
          [1 0] :deep-fried-dodo-bird
          [1 1] :tofu-and-ginger))

(defn customer-orders [party name-of-waiter]
  {:pre [
         (vector? party)
         (string? name-of-waiter)
         ]
   :post [
          (vector? %)
          (:waiter (first %))
          (:customer (first %))
          (:menu-item-name (first %))
          ]}
  (loop [customers party orders []]
    (if (seq customers)
      (recur
       (rest customers)
       (conj orders {:waiter name-of-waiter
                     :customer (str (java.util.UUID/randomUUID))
                     :menu-item-name (customer-order (first customers))}))
      orders)))

(defn waiter 
  [parties]
  {:pre [
         (vector? (first parties))
         (map? (first (first parties)))
         (number? (:allergies (first (first parties))))
         (number? (:vegetarian (first (first parties))))
         ]}
  (let [name-of-waiter (str (java.util.UUID/randomUUID))]
    (doseq [party parties]
      (let [orders (customer-orders party name-of-waiter)]
        (customer/add-orders-to-customer-orders orders)
        (check-to-see-if-this-waiters-customers-are-happy)))))

(defn new-customers []
  (take (+ (rand-int 8) 1)
        (lazy-seq
         (cons
          {:allergies (rand-int 2)
           :vegetarian (rand-int 2)
           :admiration-for-quincys 100}
          (new-customers)))))

(defn new-parties []
  (map #(apply vector %)
       (take (rand-int 3)
             (lazy-seq
              (cons
               (new-customers)
               (new-parties))))))

(defn start []
  (try 
    (loop [parties []]
      ;; slowing down the code so I can read events in the REPL
      (Thread/sleep 3000)
      (when (> (count parties) 0)
        (waiter parties))
      (recur (new-parties)))
    (catch Exception e
      (pretty/pprint (stack/parse-exception e)))))

(defn -main [& args]
  (start))

