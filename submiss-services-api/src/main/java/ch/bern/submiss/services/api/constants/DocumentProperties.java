/*
 *  Submiss, eProcurement system for managing tenders
 *  Copyright (C) 2019 Stadt Bern
 *  Licensed under the EUPL, Version 1.2 or - as soon as they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at:
 *  https://joinup.ec.europa.eu/collection/eupl
 *  Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 *  distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the Licence for the specific language governing permissions and limitations
 *  under the Licence.
 */

package ch.bern.submiss.services.api.constants;

public enum DocumentProperties {

  BOLD_HEADER("boldHeader"),
  FONTS("fonts"),
  FONT_SIZE("fontSize"),
  FONT_COLOR("fontColor"),
  REMOVE_BORDER("removeBorder"),
  TABLE_POSITION("tablePosition"),
  BOTTOM_MARGIN("bottomMargin"),
  TOP_MARGIN("topMargin"),
  RIGHT_MARGIN("rightMargin"),
  LEFT_MARGIN("leftMargin"),
  BOLD_CONTENT("boldContent"),
  WIDTH("width"),
  VALUE("value"),
  SPACING("spacing"),
  ALIGN_RIGHT("alignRight"),
  ALIGN_CENTER("alignCenter"),
  ARIAL("Arial"),
  BORDER_SPACE("borderSpace"),
  REPEAT_HEADER("repeatHeader"),
  ITALIC("italic"),
  IMAGE("image"),
  TABLE_TITLE_GRIDSPAN("tableTitleGridSpan"),
  TABLE_TITLE_FONT_SIZE("tableTitleFontSize"),
  
  NUMBER_1("1"),
  NUMBER_2("2"),
  NUMBER_3("3"),
  NUMBER_4("4"),
  NUMBER_5("5"),
  NUMBER_6("6"),
  NUMBER_7("7"),
  NUMBER_8("8"),
  NUMBER_9("9"),
  NUMBER_10("10"),
  NUMBER_16("16"),
  NUMBER_20("20"),
  NUMBER_24("24"),
  NUMBER_60("60"),
  NUMBER_160("160"),
  NUMBER_200("200"),
  NUMBER_240("240"),
  NUMBER_260("260"),
  NUMBER_340("340"),
  NUMBER_535("535"),
  NUMBER_708("708"),
  NUMBER_710("710"),
  NUMBER_800("800"),
  NUMBER_850("850"),
  NUMBER_990("990"),
  NUMBER_1240("1240"),
  NUMBER_1560("1560"),
  NUMBER_1700("1700"),
  NUMBER_1985("1985"),
  NUMBER_2130("2130"),
  NUMBER_3700("3700"),
  NUMBER_3825("3825"),
  NUMBER_3970("3970"),
  NUMBER_4360("4360"),
  NUMBER_9325("9325"),
  NUMBER_13040("13040"),
  
  TENANT_STADT_BERN("Stadt Bern"),
  TENANT_EWB("EWB"),
  TENANT_KANTON_BERN("Kanton Bern");
  
  
  private String value;

  private DocumentProperties(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
