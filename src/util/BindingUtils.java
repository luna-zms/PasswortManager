package util;

import java.util.Arrays;
import java.util.function.Supplier;
import javafx.beans.Observable;
import java.util.function.Function;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;

public class BindingUtils {
    private BindingUtils() {
    }

    public static <T, V> ObjectBinding<V> makeBinding(
            ObservableValue<T> observableValue,
            Function<T, V> binder,
            Supplier<V> defaultValue,
            Observable... additionalDependencies
    ) {
        int length = additionalDependencies.length;
        Observable[] dependencies = Arrays.copyOf(additionalDependencies, length + 1);
        dependencies[length] = observableValue;

        return Bindings.createObjectBinding(() -> {
            T value = observableValue.getValue();
            return value != null ? binder.apply(value) : defaultValue.get();
        }, dependencies);
    }

    public static <T, V> ObjectBinding<V> makeBinding(
            ObservableValue<T> observableValue,
            Function<T, V> binder,
            V defaultValue,
            Observable... additionalDependencies
    ) {
        return makeBinding(observableValue,
                           binder,
                           (Supplier<V>) () -> defaultValue,
                           additionalDependencies);
    }

    public static <V> ObjectBinding<V> makeStaticBinding(
            ObservableValue<?> observableValue,
            V nonNullValue,
            V nullValue
    ) {
        return makeBinding(observableValue, (obj) -> nonNullValue, nullValue);
    }

    // TODO: Add more overloads as I need them
}
