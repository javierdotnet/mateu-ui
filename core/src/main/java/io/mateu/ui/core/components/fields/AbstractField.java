package io.mateu.ui.core.components.fields;

import io.mateu.ui.core.components.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public abstract class AbstractField<T extends AbstractField<T>> extends BaseComponent implements Component {

    private String id;

    private Label label;

    private String help;

    private String errorMessage;

    private LabelDisposition labelDisposition;

    private boolean required;

    private List<FieldValidator> validators = new ArrayList<>();


    public AbstractField(String id) {
        this.id = id;
    }

    public AbstractField(String id, String label) {
        this.id = id;
        this.label = new Label(label);
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        validators.forEach(v -> {
            String error = v.validate(this);
            if (error != null) errors.add(error);
        });
        return errors;
    };

    public Label getLabel() {
        return label;
    }

    public T setLabel(String label) {
        this.label = new Label(label);
        return (T) this;
    }

    public String getHelp() {
        return help;
    }

    public T setHelp(String help) {
        this.help = help;
        return (T) this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public T setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return (T) this;
    }

    public LabelDisposition getLabelDisposition() {
        return labelDisposition;
    }

    public T setLabelDisposition(LabelDisposition labelDisposition) {
        this.labelDisposition = labelDisposition;
        return (T) this;
    }

    public boolean isRequired() {
        return required;
    }

    public T setRequired(boolean required) {
        this.required = required;
        return (T) this;
    }

    public List<FieldValidator> getValidators() {
        return validators;
    }

    public T setValidators(List<FieldValidator> validators) {
        this.validators = validators;
        return (T) this;
    }

    public String getId() {
        return id;
    }

}
