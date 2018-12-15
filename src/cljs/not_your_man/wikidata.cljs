(ns not-your-man.wikidata)

(def nspace "http://www.wikidata.org/prop/direct/")

(def classes
  ["wd:Q212920" ; Home applicance
   "wd:Q23698381" ; Information object
   "wd:Q2424752" ; Product
   "wd:Q3314483" ; Fruit
   "wd:Q39546" ; Tool
   "wd:Q2095" ; Food
   "wd:Q43229" ; Organization
   "wd:Q8205328" ; Artificial physical object
   "wd:Q11004"]) ; Vegetable

(def properties
  {; Location
   ::P276 {:article? true
           :verb "is located at"}
   ; Subclass of
   ::P279 {:article? true
           :verb "is"}
   ; Use
   ::P366 {:verb "is used for"}
   ; Colour
   ::P462 {:verb "is"}
   ; Country of origin
   ::P495 {:verb "is from"}
   ; Has part
   ::P527 {:article? true
           :verb "has"}
   ; Different from 
   ::P1889 {:article? true
            :verb "is not"}
   ; Uses
   ::P2283 {:verb "uses"}})
