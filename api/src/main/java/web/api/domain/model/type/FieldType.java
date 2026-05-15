package web.api.domain.model.type;

public enum FieldType {
    DIRECT_IDENTIFIER, //Strong PII -> ARX suppress/generalize
    QUASI_IDENTIFIER, //Weak PII -> ARX generalize
    SENSITIVE, //Clinical content -> ARX protect strongly
    NARRATIVE, //Free text -> Presidio + Ollama
    UNKNOWN, //Fallback -> Presidio + Ollama, but with alert
    INSENSITIVE_ATTRIBUTE //Safe metadata -> Keep as‑is
}