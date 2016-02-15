(ns quincy-the-game-1.customer
  (:import [java.util UUID])
  (:require
   [clojure.pprint :as pretty]))


;; You can consider this file an example of a Pseudo Object Oriented style,
;; in that we are putting two vars into their own namespace and marking them
;; private, to limit access to them. The functions are public, and thus other
;; developers should see that we want them to interact with these vars by
;; using the public functions. There are two things to keep in mind about
;; this style:
;;
;; 1.) this isn't Object Oriented code. We don't instantiate a namespace,
;; as we would a class. At most, this imitates what would be called a 
;; Singleton if this were Object Oriented code. This is the kind of flexibility
;; we should want from our computer programming languages: we can borrow
;; any idea, from any paradigm, whenever it seems appropriate, but without
;; having to implement the whole of that paradigm. In particular, we do not
;; have to buy into the ideology that "Everything is an Object." 
;;
;; 2.) You can easily get around the privacy of a so-called "private" var,
;; so marking a var as private is really just a notation to other programmers
;; that you want them to use whatever functions you've made available in the
;; namespace. If you would like to see how to get around the privacy of a var,
;; see Christopher Maier's post on the subject: 
;;
;; http://christophermaier.name/blog/2011/04/30/not-so-private-clojure-functions
;;



(def ^:private customer-orders (ref []))
(def ^:private customer-orders-cancelled (ref [])) 


(defn add-orders-to-customer-orders [orders]
  {:pre [
         (vector? orders)
         (map? (first orders))
         (:waiter (first orders))
         (:customer (first orders))
         (:menu-item-name (first orders))
         ]}
  (dosync
   (alter customer-orders
          (fn [previous-customer-orders]
            (apply conj previous-customer-orders orders)))))

(defn change-customer-orders [old-order new-order]
  "Everything in dosync is a transaction, and just like a database transaction, it must succeed completely or fail completely. If an exception were thrown in remove-order-from-customer-orders, then customer-orders-cancelled would not be changed. "
  (println "We are going to remove this order: ")
  (pretty/pprint old-order)
  (println "We are going to add this order: ")
  (pretty/pprint new-order)
  (dosync
   (alter customer-orders #(remove #{old-order} %))
   (alter customer-orders conj new-order)
   (alter customer-orders-cancelled conj old-order))
  (println "The customer-orders: ")
  (pretty/pprint @customer-orders)
  (println "The customer-orders-cancelled: ")
  (pretty/pprint @customer-orders-cancelled))

(defn orders []
  @customer-orders)

