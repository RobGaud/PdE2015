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
 * Model definition for InfoGiocatoreBean.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the pdE2015. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class InfoGiocatoreBean extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String email;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String fotoProfilo;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String nome;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String ruoloPreferito;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String telefono;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getEmail() {
    return email;
  }

  /**
   * @param email email or {@code null} for none
   */
  public InfoGiocatoreBean setEmail(java.lang.String email) {
    this.email = email;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getFotoProfilo() {
    return fotoProfilo;
  }

  /**
   * @param fotoProfilo fotoProfilo or {@code null} for none
   */
  public InfoGiocatoreBean setFotoProfilo(java.lang.String fotoProfilo) {
    this.fotoProfilo = fotoProfilo;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getNome() {
    return nome;
  }

  /**
   * @param nome nome or {@code null} for none
   */
  public InfoGiocatoreBean setNome(java.lang.String nome) {
    this.nome = nome;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getRuoloPreferito() {
    return ruoloPreferito;
  }

  /**
   * @param ruoloPreferito ruoloPreferito or {@code null} for none
   */
  public InfoGiocatoreBean setRuoloPreferito(java.lang.String ruoloPreferito) {
    this.ruoloPreferito = ruoloPreferito;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getTelefono() {
    return telefono;
  }

  /**
   * @param telefono telefono or {@code null} for none
   */
  public InfoGiocatoreBean setTelefono(java.lang.String telefono) {
    this.telefono = telefono;
    return this;
  }

  @Override
  public InfoGiocatoreBean set(String fieldName, Object value) {
    return (InfoGiocatoreBean) super.set(fieldName, value);
  }

  @Override
  public InfoGiocatoreBean clone() {
    return (InfoGiocatoreBean) super.clone();
  }

}
