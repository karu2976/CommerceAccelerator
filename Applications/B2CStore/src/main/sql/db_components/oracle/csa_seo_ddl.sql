


--  @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/src/main/sql/ddlgen/csa_seo_ddl.xml#1 $$Change: 1385662 $

create table csa_seo_xlate (
	translation_id	varchar2(40)	not null,
	title	varchar2(254)	null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null,
	keywords	varchar2(254)	null
,constraint csa_seo_xlate_pk primary key (translation_id));


create table csa_seo_seo_xlate (
	seo_tag_id	varchar2(40)	not null,
	locale	varchar2(40)	not null,
	translation_id	varchar2(40)	not null
,constraint csa_seo_tag_xlt_pk primary key (seo_tag_id,locale)
,constraint csa_seo_tag_xlt_fk foreign key (translation_id) references csa_seo_xlate (translation_id));

create index csa_seo_tag_xlt_id on csa_seo_seo_xlate (translation_id);



