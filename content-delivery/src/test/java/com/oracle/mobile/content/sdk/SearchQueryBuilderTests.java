/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.mobile.content.sdk;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.oracle.content.sdk.model.field.FieldName;
import com.oracle.content.sdk.request.core.SearchQueryBuilder;

import static org.junit.Assert.assertEquals;

/**
 * Because {@link SearchQueryBuilder} is
 * just about string manipulation, these tests do not require any server
 * interaction and therefore do not extend from {@link SDKBaseTest}
 */
public class SearchQueryBuilderTests {

    @Test
    public void testTypeEq()  {
        String searchQuery =
                new SearchQueryBuilder("typevalue").build();

        System.out.println(searchQuery);
        assertEquals("type eq \"typevalue\"", searchQuery);
    }

    @Test
    public void testNameSimilar()  {
        String searchQuery =
            new SearchQueryBuilder().startExpression(
                FieldName.NAME.getValue(),
                SearchQueryBuilder.QueryOperator.SIMILAR,
            "namevalue").build();

        System.out.println(searchQuery);
        assertEquals("name sm \"namevalue\"", searchQuery);
    }

    @Test
    public void testNameAndDescription()  {

        String searchQuery =
                new SearchQueryBuilder().startExpression(
                        FieldName.NAME.getValue(),
                        SearchQueryBuilder.QueryOperator.EQUALS,
                        "namevalue").andField(
                                FieldName.DESCRIPTION.getValue(),
                        SearchQueryBuilder.QueryOperator.CONTAINS,
                        "descriptionvalue").build();

        System.out.println(searchQuery);
        assertEquals("name eq \"namevalue\" AND description co \"descriptionvalue\"", searchQuery);
    }

    @Test
    public void testMatchIdList1()  {

        List<String> stringList = new ArrayList<>();
        stringList.add("COREAF29AC6ACA9644F9836E36C7B558F316");

        String searchQuery = SearchQueryBuilder.matchIdList(FieldName.ID.getValue(), stringList, false);

        System.out.println(searchQuery);
        assertEquals("id eq \"COREAF29AC6ACA9644F9836E36C7B558F316\"", searchQuery);
    }

    @Test
    public void testMatchIdList2()  {

        List<String> stringList = Arrays.asList("COREAF29AC6ACA9644F9836E36C7B558F316", "COREAF29AC6ACA9644F9836E36C7B558F987");

        String searchQuery = SearchQueryBuilder.matchIdList(FieldName.ID.getValue(), stringList, false);

        System.out.println(searchQuery);
        assertEquals("id eq \"COREAF29AC6ACA9644F9836E36C7B558F316\" OR id eq \"COREAF29AC6ACA9644F9836E36C7B558F987\"", searchQuery);
    }


    @Test
    public void testReservedNames()  {

        String searchQuery = new SearchQueryBuilder().
                startExpression(FieldName.REPOSITORY_ID.getValue(),
                        SearchQueryBuilder.QueryOperator.EQUALS,
                        "F29AC6ACA9644F9836E36C7B558F316").build();


        System.out.println(searchQuery);
        assertEquals("repositoryId eq \"F29AC6ACA9644F9836E36C7B558F316\"", searchQuery);
    }




    @Test
    public void testTypeANDField()  {

        String searchQuery =
                new SearchQueryBuilder("typevalue").andField(
                        "typefield",
                        SearchQueryBuilder.QueryOperator.EQUALS, "typefieldvalue").build();

        System.out.println(searchQuery);
        assertEquals("type eq \"typevalue\" AND fields.typefield eq \"typefieldvalue\"", searchQuery);
    }

    @Test
    public void testANDFieldNestedField() {

        String searchQuery =
                new SearchQueryBuilder("typevalue")
                        .startGroup(SearchQueryBuilder.QueryOperator.AND)
                        .orField(
                                "typefield", SearchQueryBuilder.QueryOperator.EQUALS, "value1")
                        .orField(
                                "typefield", SearchQueryBuilder.QueryOperator.EQUALS, "value2")
                        .endGroup()
                        .build();

        System.out.println(searchQuery);
        assertEquals("type eq \"typevalue\" AND (fields.typefield eq \"value1\" OR fields.typefield eq \"value2\")", searchQuery);
    }

    @Test
    public void testNumberRangeField() {

        String searchQuery =
                new SearchQueryBuilder("typevalue")
                        .startGroup(SearchQueryBuilder.QueryOperator.AND)
                        .andField(
                                "numfield", SearchQueryBuilder.QueryOperator.GREATER_OR_EQUAL, "20")
                        .andField(
                                "numfield", SearchQueryBuilder.QueryOperator.LESS_OR_EQUAL, "100")
                        .endGroup()
                        .build();

        System.out.println(searchQuery);
        assertEquals("type eq \"typevalue\" AND (fields.numfield ge \"20\" AND fields.numfield le \"100\")", searchQuery);
    }

    @Test
    public void testComplexExpression() {

        String searchQuery =
                new SearchQueryBuilder("Employee")
                        .startGroup(SearchQueryBuilder.QueryOperator.AND)
                        .andField(
                                "name", SearchQueryBuilder.QueryOperator.EQUALS, "Joe")
                        .startGroup(SearchQueryBuilder.QueryOperator.OR)
                        .andField(
                                "age", SearchQueryBuilder.QueryOperator.GREATER_OR_EQUAL, "21")
                        .andField(
                                "age", SearchQueryBuilder.QueryOperator.LESS_OR_EQUAL, "30")
                        .endGroup()
                        .endGroup()
                        .orField("name", SearchQueryBuilder.QueryOperator.EQUALS, "Mary")
                        .build();

        System.out.println(searchQuery);
        assertEquals("type eq \"Employee\" AND (name eq \"Joe\" OR (fields.age ge \"21\" AND fields.age le \"30\")) OR name eq \"Mary\"", searchQuery);
    }

    // validates ((A eq 1 or A eq 2 or A eq 3) and B eq 4) or C eq 5
    @Test
    public void  testAppendToMatchListWithGrouping() {

        String searchQuery =
                new SearchQueryBuilder()
                .startGroup()
                .matchList("A", Arrays.asList("1", "2", "3"), true)
                .andField("B", SearchQueryBuilder.QueryOperator.EQUALS, "4")
                .endGroup()
                .orField("C", SearchQueryBuilder.QueryOperator.EQUALS, "5")
                .build();

        String expected = "((fields.A eq \"1\" OR fields.A eq \"2\" OR fields.A eq \"3\") AND fields.B eq \"4\") OR fields.C eq \"5\"";
        assertEquals(expected, searchQuery);
    }

    // tests the getFieldList method
    @Test
    public void testGetFieldList() {
        List<String> testList = Arrays.asList("name", "id", "custom1", "custom2");
        String result = SearchQueryBuilder.getFieldList(testList);
        assertEquals("name,id,fields.custom1,fields.custom2", result);
    }

}
