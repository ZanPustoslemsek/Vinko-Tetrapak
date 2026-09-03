
BASEDIR = .
CLASSES_HOME = $(BASEDIR)/classes
SRC_HOME = $(BASEDIR)/src

JC = javac 
JC_FLAGS =  -encoding UTF-8 -d $(CLASSES_HOME) -sourcepath $(SRC_HOME)
JC_ALL = $(JC) $(JC_FLAGS)

default: compile

re: clean compile

compile: \
	comp_start \
	mkdir \
    $(CLASSES_HOME)/s12345678/Testko.class $(CLASSES_HOME)/ogrodje/ImenskaPlosca.class $(CLASSES_HOME)/ogrodje/GUI.class $(CLASSES_HOME)/ogrodje/Nabor.class $(CLASSES_HOME)/ogrodje/CasovnaPlosca.class $(CLASSES_HOME)/ogrodje/SpodnjaPlosca.class $(CLASSES_HOME)/ogrodje/Statistika.class $(CLASSES_HOME)/ogrodje/Izid.class $(CLASSES_HOME)/ogrodje/Besedilno.class $(CLASSES_HOME)/ogrodje/Razporeditev.class $(CLASSES_HOME)/ogrodje/Igra.class $(CLASSES_HOME)/ogrodje/Tetrapak.class $(CLASSES_HOME)/ogrodje/Igralec.class $(CLASSES_HOME)/ogrodje/Razno.class $(CLASSES_HOME)/ogrodje/StatusnaPlosca.class $(CLASSES_HOME)/ogrodje/ZgornjaPlosca.class $(CLASSES_HOME)/ogrodje/Parametri.class $(CLASSES_HOME)/s00000000/Pocasko.class $(CLASSES_HOME)/s99999999/Napacko.class $(CLASSES_HOME)/skupno/Stroj.class $(CLASSES_HOME)/skupno/Postavitev.class $(CLASSES_HOME)/skupno/Matrika.class $(CLASSES_HOME)/skupno/Liki.class \
	comp_done

comp_start:
	@echo "Compiling..."

comp_done:
	@echo "Done..."

mkdir:
	@mkdir -p $(CLASSES_HOME)

$(CLASSES_HOME)/s12345678/Testko.class : $(SRC_HOME)/s12345678/Testko.java
	@echo s12345678/Testko.java
	@$(JC_ALL) $(SRC_HOME)/s12345678/Testko.java

$(CLASSES_HOME)/ogrodje/ImenskaPlosca.class : $(SRC_HOME)/ogrodje/ImenskaPlosca.java
	@echo ogrodje/ImenskaPlosca.java
	@$(JC_ALL) $(SRC_HOME)/ogrodje/ImenskaPlosca.java

$(CLASSES_HOME)/ogrodje/GUI.class : $(SRC_HOME)/ogrodje/GUI.java
	@echo ogrodje/GUI.java
	@$(JC_ALL) $(SRC_HOME)/ogrodje/GUI.java

$(CLASSES_HOME)/ogrodje/Nabor.class : $(SRC_HOME)/ogrodje/Nabor.java
	@echo ogrodje/Nabor.java
	@$(JC_ALL) $(SRC_HOME)/ogrodje/Nabor.java

$(CLASSES_HOME)/ogrodje/CasovnaPlosca.class : $(SRC_HOME)/ogrodje/CasovnaPlosca.java
	@echo ogrodje/CasovnaPlosca.java
	@$(JC_ALL) $(SRC_HOME)/ogrodje/CasovnaPlosca.java

$(CLASSES_HOME)/ogrodje/SpodnjaPlosca.class : $(SRC_HOME)/ogrodje/SpodnjaPlosca.java
	@echo ogrodje/SpodnjaPlosca.java
	@$(JC_ALL) $(SRC_HOME)/ogrodje/SpodnjaPlosca.java

$(CLASSES_HOME)/ogrodje/Statistika.class : $(SRC_HOME)/ogrodje/Statistika.java
	@echo ogrodje/Statistika.java
	@$(JC_ALL) $(SRC_HOME)/ogrodje/Statistika.java

$(CLASSES_HOME)/ogrodje/Izid.class : $(SRC_HOME)/ogrodje/Izid.java
	@echo ogrodje/Izid.java
	@$(JC_ALL) $(SRC_HOME)/ogrodje/Izid.java

$(CLASSES_HOME)/ogrodje/Besedilno.class : $(SRC_HOME)/ogrodje/Besedilno.java
	@echo ogrodje/Besedilno.java
	@$(JC_ALL) $(SRC_HOME)/ogrodje/Besedilno.java

$(CLASSES_HOME)/ogrodje/Razporeditev.class : $(SRC_HOME)/ogrodje/Razporeditev.java
	@echo ogrodje/Razporeditev.java
	@$(JC_ALL) $(SRC_HOME)/ogrodje/Razporeditev.java

$(CLASSES_HOME)/ogrodje/Igra.class : $(SRC_HOME)/ogrodje/Igra.java
	@echo ogrodje/Igra.java
	@$(JC_ALL) $(SRC_HOME)/ogrodje/Igra.java

$(CLASSES_HOME)/ogrodje/Tetrapak.class : $(SRC_HOME)/ogrodje/Tetrapak.java
	@echo ogrodje/Tetrapak.java
	@$(JC_ALL) $(SRC_HOME)/ogrodje/Tetrapak.java

$(CLASSES_HOME)/ogrodje/Igralec.class : $(SRC_HOME)/ogrodje/Igralec.java
	@echo ogrodje/Igralec.java
	@$(JC_ALL) $(SRC_HOME)/ogrodje/Igralec.java

$(CLASSES_HOME)/ogrodje/Razno.class : $(SRC_HOME)/ogrodje/Razno.java
	@echo ogrodje/Razno.java
	@$(JC_ALL) $(SRC_HOME)/ogrodje/Razno.java

$(CLASSES_HOME)/ogrodje/StatusnaPlosca.class : $(SRC_HOME)/ogrodje/StatusnaPlosca.java
	@echo ogrodje/StatusnaPlosca.java
	@$(JC_ALL) $(SRC_HOME)/ogrodje/StatusnaPlosca.java

$(CLASSES_HOME)/ogrodje/ZgornjaPlosca.class : $(SRC_HOME)/ogrodje/ZgornjaPlosca.java
	@echo ogrodje/ZgornjaPlosca.java
	@$(JC_ALL) $(SRC_HOME)/ogrodje/ZgornjaPlosca.java

$(CLASSES_HOME)/ogrodje/Parametri.class : $(SRC_HOME)/ogrodje/Parametri.java
	@echo ogrodje/Parametri.java
	@$(JC_ALL) $(SRC_HOME)/ogrodje/Parametri.java

$(CLASSES_HOME)/s00000000/Pocasko.class : $(SRC_HOME)/s00000000/Pocasko.java
	@echo s00000000/Pocasko.java
	@$(JC_ALL) $(SRC_HOME)/s00000000/Pocasko.java

$(CLASSES_HOME)/s99999999/Napacko.class : $(SRC_HOME)/s99999999/Napacko.java
	@echo s99999999/Napacko.java
	@$(JC_ALL) $(SRC_HOME)/s99999999/Napacko.java

$(CLASSES_HOME)/skupno/Stroj.class : $(SRC_HOME)/skupno/Stroj.java
	@echo skupno/Stroj.java
	@$(JC_ALL) $(SRC_HOME)/skupno/Stroj.java

$(CLASSES_HOME)/skupno/Postavitev.class : $(SRC_HOME)/skupno/Postavitev.java
	@echo skupno/Postavitev.java
	@$(JC_ALL) $(SRC_HOME)/skupno/Postavitev.java

$(CLASSES_HOME)/skupno/Matrika.class : $(SRC_HOME)/skupno/Matrika.java
	@echo skupno/Matrika.java
	@$(JC_ALL) $(SRC_HOME)/skupno/Matrika.java

$(CLASSES_HOME)/skupno/Liki.class : $(SRC_HOME)/skupno/Liki.java
	@echo skupno/Liki.java
	@$(JC_ALL) $(SRC_HOME)/skupno/Liki.java

clean:
	@echo "Cleaning..."
	@$(RM) `find $(CLASSES_HOME) -name *.class`
	@echo "Done!"
