(ns not-your-man.subs
  "Subscriptions should allow views not to have a clue about the database schema.
  However, in simple cases they end up as trivial lookups."
  (:require [not-your-man.config :as config]
            [not-your-man.wikidata :as wdt]
            [clojure.string :as string]
            [goog.string :refer [format]]
            [re-frame.core :refer [reg-sub subscribe]]))

(def compact-iri
  (let [skip (count wdt/nspace)]
    (fn [iri]
      (keyword 'not-your-man.wikidata (subs iri skip)))))

(defn add-article
  [s]
  (let [indefinite-article (if (-> s first string/lower-case #{\a \e \i \o}) "an" "a")]
    (str indefinite-article " " s)))

(defn verbalize
  [{:keys [property notyourman]}]
  (let [{:keys [article? verb]} (-> property compact-iri wdt/properties)]
    (str verb " " (if article? (add-article notyourman) notyourman))))

(reg-sub ::error (comp :status-text :error))

(reg-sub ::loading?
  (fn [{:keys [loading?]}]
    loading?))

(reg-sub ::modal? (some-fn :error :loading?))

(reg-sub ::subject :subject)

(reg-sub ::actual-subject
  (fn [{:keys [label]}]
    (if label (add-article label) "something else")))

(reg-sub ::thing 
  (fn [{:keys [thing]}]
    thing))

(reg-sub ::verdict
  (fn [& _]
    [(subscribe [::subject])
     (subscribe [::actual-subject])])
  (fn [[subject actual-subject] _]
    [(format "He's not your %s. He's " subject) actual-subject]))

(reg-sub ::not-your-man
   (fn [{:keys [not-your-man]}]
     (map verbalize not-your-man)))

(reg-sub ::tweet
   (fn [& _]
     [(subscribe [::subject])
      (subscribe [::not-your-man])
      (subscribe [::verdict])])
   (fn [[subject not-your-man [verdict actual-subject]] _]
     (let [lines (concat [(str config/preamble " " subject ":\n")]
                         (map (partial str "- ") not-your-man)
                         [(str \newline verdict actual-subject ".")])]
      (->> lines
           (string/join \newline)
           js/encodeURIComponent
           (str "https://twitter.com/intent/tweet?text=")))))
