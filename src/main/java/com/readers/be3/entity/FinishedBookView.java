package com.readers.be3.entity;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Immutable
@Table(name="finished_book_view")
public class FinishedBookView {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="ui_seq") private Long uiSeq;
  @Column(name="bi_seq") private Long biSeq;
  @Column(name="bi_name") private String biName;
  @Column(name="bi_author") private String biAuthor;
  @Column(name="bi_publisher") private String biPublisher;
  @Column(name="si_status") private Integer siStatus;
  @Column(name="bimg_uri") private String bimgUri;
}
