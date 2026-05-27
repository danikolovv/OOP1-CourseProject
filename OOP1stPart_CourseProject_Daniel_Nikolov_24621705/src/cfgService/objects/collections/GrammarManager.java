package cfgService.objects.collections;

import cfgService.objects.Grammar;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GrammarManager {
    private static Map<Integer, Grammar> grammars;
    private static GrammarManager grammarManager;

    private GrammarManager() {
        grammars = new LinkedHashMap<Integer, Grammar>();
    }

    public static GrammarManager getInstance() {
        if (grammarManager == null) {
            grammarManager = new GrammarManager();
        }
        return grammarManager;
    }

    public void addGrammar(int id, Grammar grammar) {
        grammars.putIfAbsent(id, grammar);
    }

    private static int countAllRules() {
        int allRulesCount = 0;
        List<Grammar> allGrammars = grammars.values().stream().toList();

        for (Grammar grammar1 : allGrammars) {
            allRulesCount += grammar1.getRules().size();
        }

        return allRulesCount;
    }

    // getters
    public Grammar getGrammar(int id) {
        Grammar grammar = null;

        if (!grammars.containsKey(id)) {
            this.addGrammar(id, new Grammar(id, this));
        }
        grammar = grammars.get(id);

        return grammar;
    }

    public List<Map.Entry<Integer, Grammar>> getAllGrammars() {
        return grammars.entrySet().stream().toList();
    }

    public List<Grammar> getGrammarValuesOnly() {
        return grammars.values().stream().toList();
    }

    public void clearPresentData() {
        grammars.clear();
    }

}
