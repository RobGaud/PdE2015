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
 * on 2015-08-02 at 10:02:40 UTC 
 * Modify at your own risk.
 */

package com.appspot.futsalapp_1008.pdE2015.model;

/**
 * Model definition for Gruppo.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the pdE2015. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Gruppo extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String citta;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private com.google.api.client.util.DateTime dataCreazione;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.util.List<java.lang.Long> giocatoriIscritti;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long linkGestito;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String nome;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.util.List<java.lang.Long> partiteOrganizzate;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCitta() {
    return citta;
  }

  /**
   * @param citta citta or {@code null} for none
   */
  public Gruppo setCitta(java.lang.String citta) {
    this.citta = citta;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public com.google.api.client.util.DateTime getDataCreazione() {
    return dataCreazione;
  }

  /**
   * @param dataCreazione dataCreazione or {@code null} for none
   */
  public Gruppo setDataCreazione(com.google.api.client.util.DateTime dataCreazione) {
    this.dataCreazione = dataCreazione;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.Long> getGiocatoriIscritti() {
    return giocatoriIscritti;
  }

  /**
   * @param giocatoriIscritti giocatoriIscritti or {@code null} for none
   */
  public Gruppo setGiocatoriIscritti(java.util.List<java.lang.Long> giocatoriIscritti) {
    this.giocatoriIscritti = giocatoriIscritti;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public Gruppo setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getLinkGestito() {
    return linkGestito;
  }

  /**
   * @param linkGestito linkGestito or {@code null} for none
   */
  public Gruppo setLinkGestito(java.lang.Long linkGestito) {
    this.linkGestito = linkGestito;
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
  public Gruppo setNome(java.lang.String nome) {
    this.nome = nome;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.Long> getPartiteOrganizzate() {
    return partiteOrganizzate;
  }

  /**
   * @param partiteOrganizzate partiteOrganizzate or {@code null} for none
   */
  public Gruppo setPartiteOrganizzate(java.util.List<java.lang.Long> partiteOrganizzate) {
    this.partiteOrganizzate = partiteOrganizzate;
    return this;
  }

  @Override
  public Gruppo set(String fieldName, Object value) {
    return (Gruppo) super.set(fieldName, value);
  }

  @Override
  public Gruppo clone() {
    return (Gruppo) super.clone();
  }

}
