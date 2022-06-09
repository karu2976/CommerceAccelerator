package com.endeca.navigation;

import com.endeca.navigation.MockAggrERec;
import com.endeca.navigation.MockAggrERecList;
import com.endeca.navigation.MockDataFactory;
import com.endeca.navigation.MockENEQueryResults;
import com.endeca.navigation.MockERec;
import com.endeca.navigation.MockERecList;
import com.endeca.navigation.MockNavigation;
import com.endeca.navigation.MockProperty;
import com.endeca.navigation.MockPropertyMap;

/**
 * 
 */
public class CSAMockDataFactory {

  public static final long ID_CATEGORY_TYPE = 10011L;
  public static final String NAME_CATEGORY_TYPE = "product.category";

  public static final long ID_MEN = 8025L;
  public static final String NAME_MEN = "Men";

  public static final long ID_SHOES = 8026L;
  public static final String NAME_SHOES = "Shoes";

  public static final long ID_SHIRTS = 8027L;
  public static final String NAME_SHIRTS = "Shirts";

  public static final long ID_HOME_ACCENTS = 8028L;
  public static final String NAME_HOME_ACCENTS = "Home Accents";

  public static final long ID_GLASSWARE = 8029L;
  public static final String NAME_GLASSWARE = "Glassware";

  public static final long ID_FURNITURE = 8030L;
  public static final String NAME_FURNITURE = "Furniture";

  public static final long ID_SEATING = 8031L;
  public static final String NAME_SEATING = "Seating";
  
  public static final long ID_CHAIRS = 8032L;
  public static final String NAME_CHAIRS = "Chairs";

  public static final String KEY_FOO = "foo";
  public static final String VALUE_FOO = "bar";

  public static final String KEY_BAR = "bar";
  public static final String VALUE_BAR = "foo";

  public static final String KEY_NAME = "P_Name";

  public static final String D_GRAPH_BINS = "DGraph.Bins";
  public static final String D_GRAPH_AGGR_BINS = "DGraph.AggrBins";
  
  public static final String PRODUCT_REPOSITORY_ID = "product.repositoryId";
  public static final String SKU_REPOSITORY_ID = "sku.repositoryId";


  //======================================================================
  // DIMVALS
  //======================================================================

  /**
   * 
   */
  public DimVal createCategoryDimVal() {
    final MockDimVal dimVal = new MockDimVal();
    dimVal.setDimValName(NAME_CATEGORY_TYPE);
    dimVal.setDimValId(ID_CATEGORY_TYPE);
    dimVal.setDimensionName(NAME_CATEGORY_TYPE);
    dimVal.setDimensionId(ID_CATEGORY_TYPE);
    dimVal.setDimValFlags(4L); // navigable!!
    
    final MockPropertyMap propertyMap = new MockPropertyMap(
      new MockProperty(KEY_FOO, VALUE_FOO), new MockProperty(KEY_BAR, VALUE_BAR));
    
    dimVal.setProperties(propertyMap);
    return dimVal;
  }
  
  /**
   * 
   */
  public DimVal createMenCategoryDimVal() {
    final MockDimVal dimVal = new MockDimVal();
    dimVal.setDimValName(NAME_MEN);
    dimVal.setDimValId(ID_MEN);
    dimVal.setDimensionName(NAME_CATEGORY_TYPE);
    dimVal.setDimensionId(ID_CATEGORY_TYPE);
    dimVal.setDimValFlags(4L); // navigable!!

    final MockPropertyMap propertyMap = new MockPropertyMap(
      new MockProperty("category.repositoryId", "catMen"), new MockProperty(KEY_BAR, VALUE_BAR));
    
    dimVal.setProperties(propertyMap);
    return dimVal;
  }

  /**
   * 
   */
  public DimVal createShirtsCategoryDimVal() {
    final MockDimVal dimVal = new MockDimVal();
    dimVal.setDimValName(NAME_SHIRTS);
    dimVal.setDimValId(ID_SHIRTS);
    dimVal.setDimensionName(NAME_CATEGORY_TYPE);
    dimVal.setDimensionId(ID_CATEGORY_TYPE);
    dimVal.setDimValFlags(4L); // navigable!!
    
    final MockPropertyMap propertyMap = new MockPropertyMap(
      new MockProperty("category.repositoryId", "catMenShirts"), new MockProperty(KEY_BAR, VALUE_BAR));
    
    dimVal.setProperties(propertyMap);
    return dimVal;
  }
  
  /**
   * 
   */
  public DimVal createShoesCategoryDimVal() {
    final MockDimVal dimVal = new MockDimVal();
    dimVal.setDimValName(NAME_SHOES);
    dimVal.setDimValId(ID_SHOES);
    dimVal.setDimensionName(NAME_CATEGORY_TYPE);
    dimVal.setDimensionId(ID_CATEGORY_TYPE);
    dimVal.setDimValFlags(4L); // navigable!!
    
    final MockPropertyMap propertyMap = new MockPropertyMap(
      new MockProperty("category.repositoryId", "cat50077"), new MockProperty(KEY_BAR, VALUE_BAR));
    
    dimVal.setProperties(propertyMap);
    return dimVal;
  }
  
  /**
   * 
   */
  public DimVal createFurnitureCategoryDimVal() {
    final MockDimVal dimVal = new MockDimVal();
    dimVal.setDimValName(NAME_FURNITURE);
    dimVal.setDimValId(ID_FURNITURE);
    dimVal.setDimensionName(NAME_CATEGORY_TYPE);
    dimVal.setDimensionId(ID_CATEGORY_TYPE);
    dimVal.setDimValFlags(4L); // navigable!!
    
    final MockPropertyMap propertyMap = new MockPropertyMap(
      new MockProperty("category.repositoryId", "cat90015"), new MockProperty(KEY_BAR, VALUE_BAR));
    
    dimVal.setProperties(propertyMap);
    return dimVal;
  }
  
  /**
   * 
   */
  public DimVal createSeatingCategoryDimVal() {
    final MockDimVal dimVal = new MockDimVal();
    dimVal.setDimValName(NAME_SEATING);
    dimVal.setDimValId(ID_SEATING);
    dimVal.setDimensionName(NAME_CATEGORY_TYPE);
    dimVal.setDimensionId(ID_CATEGORY_TYPE);
    dimVal.setDimValFlags(4L); // navigable!!
    
    final MockPropertyMap propertyMap = new MockPropertyMap(
      new MockProperty("category.repositoryId", "homeStoreSeating"), new MockProperty(KEY_BAR, VALUE_BAR));
    
    dimVal.setProperties(propertyMap);
    return dimVal;
  }
  
  /**
   * 
   */
  public DimVal createChairsCategoryDimVal() {
    final MockDimVal dimVal = new MockDimVal();
    dimVal.setDimValName(NAME_CHAIRS);
    dimVal.setDimValId(ID_CHAIRS);
    dimVal.setDimensionName(NAME_CATEGORY_TYPE);
    dimVal.setDimensionId(ID_CATEGORY_TYPE);
    dimVal.setDimValFlags(4L); // navigable!!
    
    final MockPropertyMap propertyMap = new MockPropertyMap(
      new MockProperty("category.repositoryId", "homeStoreChairs"), new MockProperty(KEY_BAR, VALUE_BAR));
    
    dimVal.setProperties(propertyMap);
    return dimVal;
  }
  
  /**
   * 
   */
  public DimVal createHomeAccentsCategoryDimVal() {
    final MockDimVal dimVal = new MockDimVal();
    dimVal.setDimValName(NAME_HOME_ACCENTS);
    dimVal.setDimValId(ID_HOME_ACCENTS);
    dimVal.setDimensionName(NAME_CATEGORY_TYPE);
    dimVal.setDimensionId(ID_CATEGORY_TYPE);
    dimVal.setDimValFlags(4L); // navigable!!
    
    final MockPropertyMap propertyMap = new MockPropertyMap(
      new MockProperty("category.repositoryId", "cat10016"), new MockProperty(KEY_BAR, VALUE_BAR));
    
    dimVal.setProperties(propertyMap);
    return dimVal;
  }
  
  /**
   * 
   */
  public DimVal createGlasswareCategoryDimVal() {
    final MockDimVal dimVal = new MockDimVal();
    dimVal.setDimValName(NAME_SHOES);
    dimVal.setDimValId(ID_SHOES);
    dimVal.setDimensionName(NAME_CATEGORY_TYPE);
    dimVal.setDimensionId(ID_CATEGORY_TYPE);
    dimVal.setDimValFlags(4L); // navigable!!
    
    final MockPropertyMap propertyMap = new MockPropertyMap(
      new MockProperty("category.repositoryId", "cat10024"), new MockProperty(KEY_BAR, VALUE_BAR));
    
    dimVal.setProperties(propertyMap);
    return dimVal;
  }

  //======================================================================
  // DIMVALLISTS
  //======================================================================

  /**
   * 
   */
  public DimValList createCategoryTypeMenDimValList() {
    final MockDimValList dimValList = new MockDimValList();
    dimValList.appendDimVal(createMenCategoryDimVal());

    return dimValList;
  }

  /**
   * 
   */
  public DimValList createCategoryTypeShirtsDimValList() {
    final MockDimValList dimValList = new MockDimValList();
    dimValList.appendDimVal(createShirtsCategoryDimVal());

    return dimValList;
  }
  
  /**
   * 
   */
  public DimValList createCategoryTypeShoesDimValList() {
    final MockDimValList dimValList = new MockDimValList();
    dimValList.appendDimVal(createShoesCategoryDimVal());

    return dimValList;
  }
  
  /**
   * 
   */
  public DimValList createCategoryTypeFurnitureDimValList() {
    final MockDimValList dimValList = new MockDimValList();
    dimValList.appendDimVal(createFurnitureCategoryDimVal());

    return dimValList;
  }

  /**
   * 
   */
  public DimValList createCategoryTypeSeatingDimValList() {
    final MockDimValList dimValList = new MockDimValList();
    dimValList.appendDimVal(createSeatingCategoryDimVal());

    return dimValList;
  }
  
  /**
   * 
   */
  public DimValList createCategoryTypeChairsDimValList() {
    final MockDimValList dimValList = new MockDimValList();
    dimValList.appendDimVal(createChairsCategoryDimVal());

    return dimValList;
  }
  
  /**
   * 
   */
  public DimValList createCategoryTypeHomeAccentsDimValList() {
    final MockDimValList dimValList = new MockDimValList();
    dimValList.appendDimVal(createHomeAccentsCategoryDimVal());

    return dimValList;
  }


  
  //======================================================================
  // DIMENSIONS
  //======================================================================

  /**
   * 
   */
  public Dimension createCategoryTypeMenDimension() {

    final MockDimension dimension = new MockDimension();
    dimension.setRootDimVal(createMenCategoryDimVal());
    dimension.setNavState(createCategoryTypeMenDimLocation());
    dimension.setDimensionFlags(1L); // contains navigation state!
    dimension.setEdgesList(createEmptyRefinements());

    return dimension;
  }
  
  /**
   * 
   */
  public Dimension createCategoryTypeShirtsDimension() {

    final MockDimension dimension = new MockDimension();
    dimension.setRootDimVal(createShirtsCategoryDimVal());
    dimension.setNavState(createCategoryTypeShirtsDimLocation());
    dimension.setDimensionFlags(1L); // contains navigation state!
    dimension.setEdgesList(createEmptyRefinements());

    return dimension;
  }

  /**
   * 
   */
  public Dimension createCategoryTypeShoesDimension() {

    final MockDimension dimension = new MockDimension();
    dimension.setRootDimVal(createShoesCategoryDimVal());
    dimension.setNavState(createCategoryTypeShoesDimLocation());
    dimension.setDimensionFlags(1L); // contains navigation state!
    dimension.setEdgesList(createEmptyRefinements());

    return dimension;
  }
  
  /**
   * 
   */
  public Dimension createCategoryTypeFurnitureDimension() {

    final MockDimension dimension = new MockDimension();
    dimension.setRootDimVal(createFurnitureCategoryDimVal());
    dimension.setNavState(createCategoryTypeFurnitureDimLocation());
    dimension.setDimensionFlags(1L); // contains navigation state!
    dimension.setEdgesList(createEmptyRefinements());

    return dimension;
  }

  /**
   * 
   */
  public Dimension createCategoryTypeSeatingDimension() {

    final MockDimension dimension = new MockDimension();
    dimension.setRootDimVal(createSeatingCategoryDimVal());
    dimension.setNavState(createCategoryTypeSeatingDimLocation());
    dimension.setDimensionFlags(1L); // contains navigation state!
    dimension.setEdgesList(createEmptyRefinements());

    return dimension;
  }
  
  /**
   * 
   */
  public Dimension createCategoryTypeChairsDimension() {

    final MockDimension dimension = new MockDimension();
    dimension.setRootDimVal(createChairsCategoryDimVal());
    dimension.setNavState(createCategoryTypeChairsDimLocation());
    dimension.setDimensionFlags(1L); // contains navigation state!
    dimension.setEdgesList(createEmptyRefinements());

    return dimension;
  }
  
  /**
   * 
   */
  public Dimension createCategoryTypeHomeAccentsDimension() {

    final MockDimension dimension = new MockDimension();
    dimension.setRootDimVal(createHomeAccentsCategoryDimVal());
    dimension.setNavState(createCategoryTypeHomeAccentsDimLocation());
    dimension.setDimensionFlags(1L); // contains navigation state!
    dimension.setEdgesList(createEmptyRefinements());

    return dimension;
  }
  
  //======================================================================
  // DIMENSIONLISTS
  //======================================================================

  /**
   * 
   */
  public DimensionList createEmptyDimensionList() {
    final MockDimensionList dimensionList = new MockDimensionList();
    return dimensionList;
  }

  /**
   * 
   */
  public DimensionList createMenDimensionList() {
    final MockDimensionList dimensionList = new MockDimensionList();
    dimensionList.appendDimension(createCategoryTypeMenDimension());

    return dimensionList;
  }
  
  /**
   * 
   */
  public DimensionList createShirtsDimensionList() {
    final MockDimensionList dimensionList = new MockDimensionList();
    dimensionList.appendDimension(createCategoryTypeShirtsDimension());

    return dimensionList;
  }

  /**
   * 
   */
  public DimensionList createShoesDimensionList() {
    final MockDimensionList dimensionList = new MockDimensionList();
    dimensionList.appendDimension(createCategoryTypeShoesDimension());

    return dimensionList;
  }

  /**
   * 
   */
  public DimensionList createFurnitureDimensionList() {
    final MockDimensionList dimensionList = new MockDimensionList();
    dimensionList.appendDimension(createCategoryTypeFurnitureDimension());

    return dimensionList;
  }

  /**
   * 
   */
  public DimensionList createSeatingDimensionList() {
    final MockDimensionList dimensionList = new MockDimensionList();
    dimensionList.appendDimension(createCategoryTypeSeatingDimension());

    return dimensionList;
  }
  
  /**
   * 
   */
  public DimensionList createChairsDimensionList() {
    final MockDimensionList dimensionList = new MockDimensionList();
    dimensionList.appendDimension(createCategoryTypeChairsDimension());

    return dimensionList;
  }
  
  /**
   * 
   */
  public DimensionList createHomeAccentsDimensionList() {
    final MockDimensionList dimensionList = new MockDimensionList();
    dimensionList.appendDimension(createCategoryTypeHomeAccentsDimension());

    return dimensionList;
  }

  

  
  //======================================================================
  // DIMLOCATIONS
  //=====================================================================

  /**
   * 
   */
  public DimLocation createCategoryTypeMenDimLocation() {
    final MockDimValList ancestors = new MockDimValList();
    final MockDimLocation dimLocation = new MockDimLocation();
    dimLocation.setDimValue(createMenCategoryDimVal());
    dimLocation.setAncestors(ancestors);

    return dimLocation;
  }

  /**
   * 
   */
  public DimLocation createCategoryTypeShirtsDimLocation() {
    final MockDimValList ancestors = new MockDimValList();
    ancestors.appendDimVal(createMenCategoryDimVal());
    final MockDimLocation dimLocation = new MockDimLocation();
    dimLocation.setDimValue(createShirtsCategoryDimVal());
    dimLocation.setAncestors(ancestors);

    return dimLocation;
  }

  /**
   * 
   */
  public DimLocation createCategoryTypeShoesDimLocation() {
    final MockDimValList ancestors = new MockDimValList();
    ancestors.appendDimVal(createMenCategoryDimVal());
    final MockDimLocation dimLocation = new MockDimLocation();
    dimLocation.setDimValue(createShoesCategoryDimVal());
    dimLocation.setAncestors(ancestors);

    return dimLocation;
  }
  
  /**
   * 
   */
  public DimLocation createCategoryTypeFurnitureDimLocation() {
    final MockDimValList ancestors = new MockDimValList();
    final MockDimLocation dimLocation = new MockDimLocation();
    dimLocation.setDimValue(createFurnitureCategoryDimVal());
    dimLocation.setAncestors(ancestors);

    return dimLocation;
  }
  
  /**
   * 
   */
  public DimLocation createCategoryTypeSeatingDimLocation() {
    final MockDimValList ancestors = new MockDimValList();
    ancestors.appendDimVal(createFurnitureCategoryDimVal());
    final MockDimLocation dimLocation = new MockDimLocation();
    dimLocation.setDimValue(createSeatingCategoryDimVal());
    dimLocation.setAncestors(ancestors);

    return dimLocation;
  }

  /**
   * 
   */
  public DimLocation createCategoryTypeChairsDimLocation() {
    final MockDimValList ancestors = new MockDimValList();
    ancestors.appendDimVal(createFurnitureCategoryDimVal());
    ancestors.appendDimVal(createSeatingCategoryDimVal());
    final MockDimLocation dimLocation = new MockDimLocation();
    dimLocation.setDimValue(createChairsCategoryDimVal());
    dimLocation.setAncestors(ancestors);

    return dimLocation;
  }
  
  /**
   * 
   */
  public DimLocation createCategoryTypeHomeAccentsDimLocation() {
    final MockDimValList ancestors = new MockDimValList();
    final MockDimLocation dimLocation = new MockDimLocation();
    dimLocation.setDimValue(createHomeAccentsCategoryDimVal());
    dimLocation.setAncestors(ancestors);

    return dimLocation;
  }


  
  //======================================================================
  // DIMLOCATIONLISTS
  //======================================================================

  /**
   * 
   */
  public DimLocationList createEmptyDimLocationList() {
    final MockDimLocationList dimLocationList = new MockDimLocationList();
    return dimLocationList;
  }

  /**
   * 
   */
  public DimLocationList createMenDimLocationList() {
    final MockDimLocationList dimLocationList = new MockDimLocationList();
    dimLocationList.appendDimLocation(createCategoryTypeMenDimLocation());

    return dimLocationList;
  }
  
  /**
   * 
   */
  public DimLocationList createShirtsDimLocationList() {
    final MockDimLocationList dimLocationList = new MockDimLocationList();
    dimLocationList.appendDimLocation(createCategoryTypeShirtsDimLocation());

    return dimLocationList;
  }

  /**
   * 
   */
  public DimLocationList createShoesDimLocationList() {
    final MockDimLocationList dimLocationList = new MockDimLocationList();
    dimLocationList.appendDimLocation(createCategoryTypeShoesDimLocation());

    return dimLocationList;
  }

  /**
   * 
   */
  public DimLocationList createFurnitureDimLocationList() {
    final MockDimLocationList dimLocationList = new MockDimLocationList();
    dimLocationList.appendDimLocation(createCategoryTypeFurnitureDimLocation());

    return dimLocationList;
  }

  /**
   * 
   */
  public DimLocationList createSeatingDimLocationList() {
    final MockDimLocationList dimLocationList = new MockDimLocationList();
    dimLocationList.appendDimLocation(createCategoryTypeSeatingDimLocation());

    return dimLocationList;
  }
  
  /**
   * 
   */
  public DimLocationList createChairsDimLocationList() {
    final MockDimLocationList dimLocationList = new MockDimLocationList();
    dimLocationList.appendDimLocation(createCategoryTypeChairsDimLocation());

    return dimLocationList;
  }

  /**
   * 
   */
  public DimLocationList createHomeAccentsDimLocationList() {
    final MockDimLocationList dimLocationList = new MockDimLocationList();
    dimLocationList.appendDimLocation(createCategoryTypeHomeAccentsDimLocation());

    return dimLocationList;
  }



  //======================================================================
  // DIMENSION REFINEMENTS
  //======================================================================

  /**
   * 
   */
  public DimValList createEmptyRefinements() {
    final MockDimValList refinements = new MockDimValList();
    return refinements;
  }

  /**
   * 
   */
  public DimValList createCategoryTypeMenRefinements() {
    final MockDimValList refinements = new MockDimValList();
    refinements.appendDimVal(createMenCategoryDimVal());

    return refinements;
  }

  /**
   * 
   */
  public DimValList createCategoryTypeShirtsRefinements() {
    final MockDimValList refinements = new MockDimValList();
    refinements.appendDimVal(createShirtsCategoryDimVal());

    return refinements;
  }
  
  /**
   * 
   */
  public DimValList createCategoryTypeShoesRefinements() {
    final MockDimValList refinements = new MockDimValList();
    refinements.appendDimVal(createShoesCategoryDimVal());

    return refinements;
  }
  
  /**
   * 
   */
  public DimValList createCategoryTypeFurnitureRefinements() {
    final MockDimValList refinements = new MockDimValList();
    refinements.appendDimVal(createFurnitureCategoryDimVal());

    return refinements;
  }
  
  /**
   * 
   */
  public DimValList createCategoryTypeSeatingRefinements() {
    final MockDimValList refinements = new MockDimValList();
    refinements.appendDimVal(createSeatingCategoryDimVal());

    return refinements;
  }
  
  /**
   * 
   */
  public DimValList createCategoryTypeChairsRefinements() {
    final MockDimValList refinements = new MockDimValList();
    refinements.appendDimVal(createChairsCategoryDimVal());

    return refinements;
  }
  
  /**
   * 
   */
  public DimValList createCategoryTypeHomeAccentsRefinements() {
    final MockDimValList refinements = new MockDimValList();
    refinements.appendDimVal(createHomeAccentsCategoryDimVal());

    return refinements;
  }
  
  //======================================================================
  // ERECS
  //======================================================================
  
  public ERec createMockProductERec(final String pId) {

    final MockPropertyMap properties = new MockPropertyMap(
        new MockProperty(PRODUCT_REPOSITORY_ID, pId));

    final MockAssocDimLocations productLocations = new MockAssocDimLocations();
    productLocations.setDimRoot(createShirtsCategoryDimVal());
    productLocations.setDimLocationList(createShirtsDimLocationList());

    final MockAssocDimLocationsList dimValues = new MockAssocDimLocationsList();
    dimValues.appendAssocDimLocations(productLocations);

    final MockERec erec = new MockERec();
    erec.setProperties(properties);
    erec.setDimValues(dimValues);
    erec.setSpec(pId);

    return erec;
  }
  
  public ERec createMockSkuERec(final String pId) {

    final MockPropertyMap properties = new MockPropertyMap(
        new MockProperty(SKU_REPOSITORY_ID, pId));

    final MockAssocDimLocations skuLocations = new MockAssocDimLocations();
    skuLocations.setDimRoot(createShirtsCategoryDimVal());
    skuLocations.setDimLocationList(createShirtsDimLocationList());

    final MockAssocDimLocationsList dimValues = new MockAssocDimLocationsList();
    dimValues.appendAssocDimLocations(skuLocations);

    final MockERec erec = new MockERec();
    erec.setProperties(properties);
    erec.setDimValues(dimValues);
    erec.setSpec(pId);

    return erec;
  }
  
  //======================================================================
  // OTHERS
  //======================================================================

  /**
   * 
   */
  public MockDimensionSearchResultGroup createDimensionSearchResultGroup(
      DimVal rootDimVal, DimLocationList ... dimVals) {

    MockDimensionSearchResultGroup group = new MockDimensionSearchResultGroup();
    group.setRoot(rootDimVal);

    for (DimLocationList dlList : dimVals) {
        group.add(dlList);
    }

    MockDimensionSearchResultGroupList results = new MockDimensionSearchResultGroupList();
    results.add(group);

    return group;
  }

}

