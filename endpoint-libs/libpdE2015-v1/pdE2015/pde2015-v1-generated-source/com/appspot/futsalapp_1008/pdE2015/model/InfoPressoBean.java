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
 * on 2015-07-30 at 15:18:07 UTC 
 * Modify at your own risk.
 */

package com.appspot.futsalapp_1008.pdE2015.model;

/**
 * Model definition for InfoPressoBean.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the pdE2015. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class InfoPressoBean extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long campo;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long partita;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getCampo() {
    return campo;
  }

  /**
   * @param campo campo or {@code null} for none
   */
  public InfoPressoBean setCampo(java.lang.Long campo) {
    this.campo = campo;
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
  public InfoPressoBean setPartita(java.lang.Long partita) {
    this.partita = partita;
    return this;
  }

  @Override
  public InfoPressoBean set(String fieldName, Object value) {
    return (InfoPressoBean) super.set(fieldName, value);
  }

  @Override
  public InfoPressoBean clone() {
    return (InfoPressoBean) super.clone();
  }

}
