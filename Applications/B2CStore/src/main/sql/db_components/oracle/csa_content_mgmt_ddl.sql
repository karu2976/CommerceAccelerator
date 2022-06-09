


--  @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/src/main/sql/ddlgen/csa_content_mgmt_ddl.xml#1 $$Change: 1385662 $

create table csa_media_content (
	id	varchar2(40)	not null,
	link_url	varchar2(255)	null,
	alt_text	varchar2(255)	null,
	image_srcset	varchar2(1056)	null,
	image_text_1	varchar2(255)	null,
	image_text_2	varchar2(255)	null,
	image_text_3	varchar2(255)	null,
	image_text_4	varchar2(255)	null
,constraint csa_media_content_p primary key (id)
,constraint csa_media_content_fk foreign key (id) references wcm_media_content (id));


create table csa_media_content_xlate (
	translation_id	varchar2(40)	not null,
	name	varchar2(255)	null,
	title	varchar2(255)	null,
	description	varchar2(255)	null,
	link_url	varchar2(255)	null,
	alt_text	varchar2(255)	null,
	image_text_1	varchar2(255)	null,
	image_text_2	varchar2(255)	null,
	image_text_3	varchar2(255)	null,
	image_text_4	varchar2(255)	null
,constraint csa_media_content_xlate_p primary key (translation_id));


create table csa_media_media_xlate (
	id	varchar2(40)	not null,
	locale	varchar2(40)	not null,
	translation_id	varchar2(40)	not null
,constraint csa_media_media_xlate_p primary key (id,locale)
,constraint csa_media_media_xlate_f1 foreign key (translation_id) references csa_media_content_xlate (translation_id));

create index csa_media_media_xlate_i1 on csa_media_media_xlate (translation_id);



