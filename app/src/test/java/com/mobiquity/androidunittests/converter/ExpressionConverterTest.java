package com.mobiquity.androidunittests.converter;

import com.mobiquity.androidunittests.calculator.input.Input;
import com.mobiquity.androidunittests.calculator.input.NumericInput;
import com.mobiquity.androidunittests.calculator.input.operator.AdditionOperator;
import com.mobiquity.androidunittests.calculator.input.operator.MultiplicationOperator;
import com.mobiquity.androidunittests.calculator.input.operator.NoOpOperator;
import com.mobiquity.androidunittests.calculator.input.operator.SubtractionOperator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;
import static com.mobiquity.androidunittests.testutil.InputListSubject.inputList;

public class ExpressionConverterTest {

    @Mock SymbolToOperatorConverter operatorConverter;
    private ExpressionConverter expressionConverter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        expressionConverter = new ExpressionConverter(operatorConverter);

        Mockito.when(operatorConverter.convert(Mockito.anyString())).thenReturn(new NoOpOperator());
        Mockito.when(operatorConverter.convert("+")).thenReturn(new AdditionOperator());
        Mockito.when(operatorConverter.convert("-")).thenReturn(new SubtractionOperator());
        Mockito.when(operatorConverter.convert("*")).thenReturn(new MultiplicationOperator());
    }

    @Test
    public void testNormalize_numbers() {
        List<String> originalInput = Arrays.asList("3", "4", "5");
        List<String> expectedNormalizedInput = Arrays.asList("3", "4", "5");

        List<String> normalizedInput = expressionConverter.normalize(originalInput);
        assertThat(normalizedInput).containsExactlyElementsIn(expectedNormalizedInput);
    }

    @Test
    public void testNormalize_DontAllowLeadingAdditionOperator() {
        List<String> originalInput = Arrays.asList("+", "5");
        List<String> expectedNormalizedInput = Arrays.asList("5");
        List<String> normalizedInput = expressionConverter.normalize(originalInput);
        assertThat(normalizedInput).containsExactlyElementsIn(expectedNormalizedInput);
    }

    @Test
    public void testNormalize_DontAllowLeadingMultiplicationOperator() {
        List<String> originalInput = Arrays.asList("*", "5");
        List<String> expectedNormalizedInput = Arrays.asList("5");
        List<String> normalizedInput = expressionConverter.normalize(originalInput);
        assertThat(normalizedInput).containsExactlyElementsIn(expectedNormalizedInput);
    }

    @Test
    public void testNormalize_DontAllowMultipleOperators() {
        List<String> originalInput = Arrays.asList("5", "+", "+", "*");
        List<String> expectedNormalizedInput = Arrays.asList("5", "*");
        List<String> normalizedInput = expressionConverter.normalize(originalInput);
        assertThat(normalizedInput).containsExactlyElementsIn(expectedNormalizedInput);
    }

    @Test
    public void testNormalize_DontAllowMinusMinus() {
        List<String> originalInput = Arrays.asList("5", "-", "-", "3");
        List<String> expectedNormalizedInput = Arrays.asList("5", "-", "3");
        List<String> normalizedInput = expressionConverter.normalize(originalInput);
        assertThat(normalizedInput).containsExactlyElementsIn(expectedNormalizedInput);
    }

    @Test
    public void testNormalize_DontAllowPlusMinus() {
        List<String> originalInput = Arrays.asList("5", "+", "-", "3");
        List<String> expectedNormalizedInput = Arrays.asList("5", "-", "3");
        List<String> normalizedInput = expressionConverter.normalize(originalInput);
        assertThat(normalizedInput).containsExactlyElementsIn(expectedNormalizedInput);
    }

    @Test
    public void testNormalize_AllowMultiplyMinus() {
        List<String> originalInput = Arrays.asList("5", "*", "-", "3");
        List<String> expectedNormalizedInput = Arrays.asList("5", "*", "-", "3");
        List<String> normalizedInput = expressionConverter.normalize(originalInput);
        assertThat(normalizedInput).containsExactlyElementsIn(expectedNormalizedInput);
    }

    @Test
    public void testConvert_SingleDigitNumber() {
        List<String> normalizedInput = Arrays.asList("3");
        List<Input> expectedConvertedInput = Arrays.asList(new NumericInput(3));
        List<Input> convertedInput = expressionConverter.convert(normalizedInput);

        assertAbout(inputList()).that(convertedInput).containsExactlyInputValuesIn(expectedConvertedInput);
    }

    @Test
    public void testConvert_MultipleDigitsNumber() {
        List<String> normalizedInput = Arrays.asList("5", "4", "3", "2");
        List<Input> expectedConvertedInput = Arrays.asList(new NumericInput(5432));
        List<Input> convertedInput = expressionConverter.convert(normalizedInput);

        assertAbout(inputList()).that(convertedInput).containsExactlyInputValuesIn(expectedConvertedInput);
    }

    @Test
    public void testConvert_NegativeNumber() {
        List<String> normalizedInput = Arrays.asList("-", "3");
        List<Input> expectedConvertedInput = Arrays.asList(new NumericInput(-3));
        List<Input> convertedInput = expressionConverter.convert(normalizedInput);

        assertAbout(inputList()).that(convertedInput).containsExactlyInputValuesIn(expectedConvertedInput);
    }

    @Test
    public void testConvert_AdditionOperation() {
        List<String> normalizedInput = Arrays.asList("3", "+", "4");
        List<Input> expectedConvertedInput = Arrays.asList(
                new NumericInput(3),
                new AdditionOperator(),
                new NumericInput(4)
        );
        List<Input> convertedInput = expressionConverter.convert(normalizedInput);

        assertAbout(inputList()).that(convertedInput).containsExactlyInputValuesIn(expectedConvertedInput);
    }

    @Test
    public void testConvert_SubtractOperation() {
        List<String> normalizedInput = Arrays.asList("3", "-", "4");
        List<Input> expectedConvertedInput = Arrays.asList(
                new NumericInput(3),
                new SubtractionOperator(),
                new NumericInput(4)
        );
        List<Input> convertedInput = expressionConverter.convert(normalizedInput);

        assertAbout(inputList()).that(convertedInput).containsExactlyInputValuesIn(expectedConvertedInput);
    }

    @Test
    public void testConvert_MultiplyOperation() {
        List<String> normalizedInput = Arrays.asList("3", "*", "4");
        List<Input> expectedConvertedInput = Arrays.asList(
                new NumericInput(3),
                new MultiplicationOperator(),
                new NumericInput(4)
        );
        List<Input> convertedInput = expressionConverter.convert(normalizedInput);

        assertAbout(inputList()).that(convertedInput).containsExactlyInputValuesIn(expectedConvertedInput);
    }

    @Test
    public void testConvert_MultiplyNegativeNumberOperation() {
        List<String> normalizedInput = Arrays.asList("3", "*", "-", "4");
        List<Input> expectedConvertedInput = Arrays.asList(
                new NumericInput(3),
                new MultiplicationOperator(),
                new NumericInput(-4)
        );
        List<Input> convertedInput = expressionConverter.convert(normalizedInput);

        assertAbout(inputList()).that(convertedInput).containsExactlyInputValuesIn(expectedConvertedInput);
    }

}