/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2015-07-16 18:28:29 UTC)
 * on 2015-07-30 at 19:06:52 UTC 
 * Modify at your own risk.
 */

package com.appspot.futsalapp_1008.pdE2015.model;

/**
 * Model definition for InfoOrganizzaBean.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the pdE2015. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class InfoOrganizzaBean extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long gruppo;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long partita;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getGruppo() {
    return gruppo;
  }

  /**
   * @param gruppo gruppo or {@code null} for none
   */
  public InfoOrganizzaBean setGruppo(java.lang.Long gruppo) {
    this.gruppo = gruppo;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getPartita() {
    return partita;
  }

  /**
   * @param partita partita or {@code null} for none
   */
  public InfoOrganizzaBean setPartita(java.lang.Long partita) {
    this.partita = partita;
    return this;
  }

  @Override
  public InfoOrganizzaBean set(String fieldName, Object value) {
    return (InfoOrganizzaBean) super.set(fieldName, value);
  }

  @Override
  public InfoOrganizzaBean clone() {
    return (InfoOrganizzaBean) super.clone();
  }

}
