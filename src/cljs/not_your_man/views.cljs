(ns not-your-man.views
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [not-your-man.events :as events]
            [not-your-man.subs :as subs]
            [goog.string :refer [format]]))

(defn loading-indicator
  []
  (let [loading? (re-frame/subscribe [::subs/loading?])
        subject (re-frame/subscribe [::subs/subject])]
    (when @loading?
      [re-com/h-box
       :children [[re-com/title
                   :label (format "Loading your %s..." @subject)
                   :level :level2]
                  [re-com/throbber
                   :color "#000"]]])))

(defn error
  []
  (when-let [{:keys [status-text]} @(re-frame/subscribe [::subs/error])]
    (let [hide-error (partial re-frame/dispatch [::events/hide-error])]
      (js/setTimeout hide-error 5000)
      [re-com/alert-box
       :alert-type :danger
       :body status-text
       :closeable? true
       :heading "Damn..."
       :on-close hide-error])))

(defn modal
  []
  (when @(re-frame/subscribe [::subs/modal?])
    [re-com/modal-panel
     :child [re-com/h-box :children [[loading-indicator] [error]]]]))

(defn preamble
  []
  [re-com/title
   :label "Ladies, if your" 
   :level :level1])

(defn input
  []
  (let [subject (re-frame/subscribe [::subs/subject])]
    [re-com/input-text
     :attr {:auto-focus true}
     :change-on-blur? false
     :class "inline-input rc-title level1"
     :model subject
     :on-change (fn [value] (re-frame/dispatch [::events/change-subject value]))
     :validation-regex #"^.{0,20}$"]))

(defn not-your-man
  []
  (let [data (re-frame/subscribe [::subs/not-your-man])]
    [:ul#not-your-man.rc-title.level1
     (for [item @data]
       [:li {:key item} item])]))

(defn verdict
  []
  (let [subject (re-frame/subscribe [::subs/subject])
        actual-subject (re-frame/subscribe [::subs/actual-subject])]
    [re-com/title
     :label (format "He's not your %s, he's %s." @subject @actual-subject)
     :level :level1]))

(defn another-man
  []
  (let [subject (re-frame/subscribe [::subs/subject])]
    [re-com/md-icon-button
     :md-icon-name "zmdi-refresh"
     :on-click #(re-frame/dispatch [::events/wikidata-query])
     :size :larger
     :tooltip (str "That's not my " @subject "!")
     :tooltip-position :left-center]))

(defn tweet-button
  []
  [re-com/md-icon-button
   :md-icon-name "zmdi-twitter"
   :size :larger
   :tooltip "Tweet this wisdom!"
   :tooltip-position :right-center])

(defn buttons
  []
  [re-com/h-box
   :gap "2em"
   :justify :center
   :children [[another-man]
              [tweet-button]]])

(defn footer
  []
  [re-com/h-box
   :class "footer"
   :justify :center
   :gap "2em"
   :children [[:p "Source data from "
               [:a {:href "https://www.wikidata.org"} [:i.zmdi.zmdi-wikipedia] "ikidata"]]
              [:p "Idea from "
               [:a {:href "https://twitter.com/notyourbot1"} "Not Your Bot"]]
              [:p "Source code at "
               [:a.zmdi.zmdi-github {:href "https://github.com/jindrichmynarz/not-your-man"}]]]])

(defn main-panel
  []
  [re-com/h-box
   :justify :center
   :min-height "100vh"
   :children [[re-com/v-box
               :justify :center
               :width "66%"
               :children [[re-com/h-box
                           :align :baseline
                           :children [[preamble]
                                      [input]]]
                          [not-your-man]
                          [verdict]
                          [re-com/gap :size "2em"]
                          [buttons]
                          [re-com/gap :size "2"]
                          [footer]]]
              [modal]]])
