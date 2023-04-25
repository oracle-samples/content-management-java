/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.request.core;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.oracle.content.sdk.model.field.FieldName;
import com.oracle.content.sdk.request.SearchAssetsRequest;

/**
 * This helper class will construct the raw "filter" string to use when specifying
 * a filter for {@link SearchAssetsRequest}.  A type is usually specified
 * when creating the builder, then field expressions can be added using {@link #andField(String, QueryOperator, String)} )}
 * or {@link #orField(String, QueryOperator, String)}.  For example to construct a search query string
 * such as 'type eq "fruit" AND fields.color EQ "red"', which searches items of type 'fruit' that also
 * has a field called 'color' with the value of red.
 * <pre> {@code
 *  String redFruitFilterQuery filterBuilder =
 *      new SearchQueryBuilder("fruit").andField("color", QueryOperator.EQUALS, "red").build();
 * }</pre>
 * More complex nested expressions can be constructed using {@link #startGroup(QueryOperator)}.  For example,
 * to create a query such as 'type eq "menu_type" AND (fields.item_type eq "breakfast" OR fields.item_type eq "lunch")':
 * <pre> {@code}
 * String searchQuery =
 *   new SearchQueryBuilder("menu_type")
 *   .startGroup(SearchQueryBuilder.QueryOperator.AND)
 *    .orField(
 *     "item_type", SearchQueryBuilder.QueryOperator.EQUALS, "breakfast")
 *    .orField(
 *     "item_type", SearchQueryBuilder.QueryOperator.EQUALS, "lunch")
 *     .endGroup()
 *    .build();
 * </pre>
 */
@SuppressWarnings({"WeakerAccess","unused"})
public class SearchQueryBuilder {

    /**
     * Enumerates the possible query operator values
     */
    public enum QueryOperator {
        EQUALS("eq"),
        CONTAINS("co"),
        STARTS_WITH("sw"),
        GREATER_OR_EQUAL("ge"),
        LESS_OR_EQUAL("le"),
        GREATER_THAN("gt"),
        LESS_THAN("lt"),
        MATCHES("mt"),
        SIMILAR("sm"),
        AND("AND"),
        OR("OR");

        final String name;

        QueryOperator(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

    }

    // string buffer to construct filter
    final private StringBuffer filter = new StringBuffer();

    // keep track of nested parens so we match at end when building
    private int groupExpressionCount = 0;


    // prefix used in V1.1+ API for field references
    final private static String FIELD_PREFIX = "fields.";


    /**
     * If the field name is not a top-level name such as "type" or "name" or "description"
     * it must be prefixed with "fields."
     *
     * @param fieldName Name of field
     * @return Name of field with "fields." prefix if necessary
     */
    static public String getFieldName(String fieldName) {

        // "name", "type" and "description" are special fields, but for other fields
        // prefix with "fields." unless already prefixed
        if (fieldName == null ||
                FieldName.isReservedFieldName(fieldName) ||
                fieldName.startsWith(FIELD_PREFIX)) {
            return fieldName;
        } else {
            return FIELD_PREFIX + fieldName;
        }

    }

    // helper method to construct the var OP "value" expression
    private String expression(String variable, String operator, String value) {
        return (getFieldName(variable)) + " " + operator + " \"" + value + "\"";
    }

    // helper method to add criteria to string, clause is something like "and"
    private String getCriteria(String clause, String variable, String operator, String value) {
        return getCriteria(clause, expression(variable, operator, value));
    }

    // helper method to add criteria to string, clause is something like "and"
    private String getCriteria(String clause, String expression){
        // if we are at the very start of an expression don't add the clause
        String clausePrefix = filter.toString().endsWith("(")?"":" " + clause + " ";
        return clausePrefix + expression;
    }

    /**
     * Constructor that takes a type and starts the filter with the expression
     * type eq 'typename', where 'typename' is the parameter.  This is
     * the most common way to start an expression, but a blank expression
     * can be created as well by calling {@link #SearchQueryBuilder()} and
     * then calling {@link #startExpression(String, QueryOperator, String)}
     *
     * @param type String for the type to match
     */
    public SearchQueryBuilder(String type) {
        startExpression(FieldName.TYPE.getValue(), QueryOperator.EQUALS, type);
    }


    /**
     * Start with a blank builder
     */
    public SearchQueryBuilder() {}

    /**
     * Called to start an expression at the beginning of the filter.
     *
     * @param field name of the field to match
     * @param operator the operation to use (e.g. "co" or "eq")
     * @param value the value to match
     * @return builder
     */
    public SearchQueryBuilder startExpression(String field, QueryOperator operator, String value) {
        startExpression(expression(field, operator.name, value));
        return this;
    }

    /**
     * Called to start an expression at the beginning of the filter with a fully formed expression
     *
     * @param expression expression to start
     * @return builder
     */
    @SuppressWarnings("UnusedReturnValue")
    public SearchQueryBuilder startExpression(String expression) {
        filter.append(expression);
        return this;
    }

    /**
     * Add an additional filter expression using "and" to an expression already started.
     *
     * @param expression fully formed filter expression
     * @return builder
     */
    @SuppressWarnings("UnusedReturnValue")
    public SearchQueryBuilder addExpression(String expression) {
        filter.append(getCriteria(QueryOperator.AND.name, expression));
        return this;
    }

    /**
     * Specify your own clause such as "and", "or".  This isn't restricted to the
     * QueryOperator enum but in general should be used for clause and operators.
     *
     * @param clause field such as "and", "or"
     * @param field name of the field to match
     * @param operator the operation to use (e.g. "co" or "eq")
     * @param value the value to match
     * @return builder
     */
    public SearchQueryBuilder addClause(String clause, String field, String operator, String value) {
        filter.append(getCriteria(clause, field, operator, value));
        return this;
    }

    /**
     * Adds a filter expression of form "AND field operator value" to the
     * filter request.
     *
     * @param field name of the field to match
     * @param operator see {@link QueryOperator} for possible values
     * @param value the value to match
     * @return builder
     */
    public SearchQueryBuilder andField(String field, QueryOperator operator, String value) {
        return addClause(QueryOperator.AND.name, field, operator.name, value);
    }

    /**
     * Start a new expression group with parenthesis, for example "AND (expression)".
     *
     * @param operator Operator to proceed the group expression
     * @return builder
     */
    public SearchQueryBuilder startGroup(QueryOperator operator) {
        if (operator != null) {
            String clause = " " + operator.name + " ";
            filter.append(clause);
        }
        filter.append("(");
        groupExpressionCount++;
        return this;
    }

    public SearchQueryBuilder startGroup() {
        filter.append("(");
        groupExpressionCount++;
        return this;
    }

    /**
     * End expression group with parenthesis.
     *
     * @return builder
     */
    public SearchQueryBuilder endGroup() {
        filter.append(")");
        groupExpressionCount--;
        return this;
    }

    /**
     * Adds a filter expression of form "OR field operator value" to the filter.
     *
     * @param field name of the field to match
     * @param operator see {@link QueryOperator} for possible values
     * @param value the value to match
     * @return builder
     */
    public SearchQueryBuilder orField(String field, QueryOperator operator, String value) {
        return addClause(QueryOperator.OR.name, field, operator.name, value);
    }

    /**
     * Given a list of strings, adds to the filter a query expression to match any of the strings.  For example,
     * if string list contains "a, b, c", then and call is matchList("id", list), then result
     * would be "id eq a OR id eq b OR id eq c"
     *
     * @param field field to match such as "id"
     * @param stringList list of string values
     * @param groupExpression if true, will put expressions in a group ( expr )
     * @return matching expression.
     */
    public SearchQueryBuilder matchList(String field, List<String> stringList, boolean groupExpression) {

        filter.append(matchIdList(field, stringList, groupExpression));
        return this;
    }

    /**
     * Given a list of strings, returns a query expression to match any of the strings.  For example,
     * if string list contains "a, b, c", then and call is matchIdList("id", list), then result
     * would be "id eq a OR id eq b OR id eq c"
     *
     * @param field field to match such as "id"
     * @param stringList list of string values
     * @param groupExpression if true, will put expressions in a group ( expr )
     * @return matching expression.
     */
    public static String matchIdList(String field, List<String> stringList, boolean groupExpression) {
        SearchQueryBuilder builder = new SearchQueryBuilder();
        if (stringList == null || stringList.size() == 0)
            return "";

        ListIterator<String> iterator = stringList.listIterator();
        builder.startExpression(field, QueryOperator.EQUALS, iterator.next());
        while (iterator.hasNext()) {
            builder.orField(field, QueryOperator.EQUALS, iterator.next());
        }

        String expression = builder.build();

        return groupExpression?"(" + expression + ")":expression;
    }

    public static String getFieldList(List<String> fieldList) {
        if (fieldList == null || fieldList.isEmpty())
            return null;

        List<String> prefixedList = new ArrayList<>(fieldList.size());

        // any custom fields, must be prefixed with "fields."
        for(String field : fieldList) {
            prefixedList.add(getFieldName(field));
        }

        return String.join(",", prefixedList);
    }


    /**
     * Return the fully constructed filter string to use for search call
     *
     * @return filter expression to use for search
     */
    public String build() {
        // when finally building, make sure all matching end parenthesis are in place
        while (groupExpressionCount > 0) {
            endGroup();
        }
        return filter.toString();
    }


}

