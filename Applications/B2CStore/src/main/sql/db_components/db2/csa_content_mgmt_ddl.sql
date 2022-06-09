


--  @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/src/main/sql/ddlgen/csa_content_mgmt_ddl.xml#1 $$Change: 1385662 $

create table csa_media_content (
	id	varchar(40)	not null,
	link_url	varchar(255)	default null,
	alt_text	varchar(255)	default null,
	image_srcset	varchar(1056)	default null,
	image_text_1	varchar(255)	default null,
	image_text_2	varchar(255)	default null,
	image_text_3	varchar(255)	default null,
	image_text_4	varchar(255)	default null
,constraint csa_media_content_p primary key (id)
,constraint csa_media_content_fk foreign key (id) references wcm_media_content (id));


create table csa_media_content_xlate (
	translation_id	varchar(40)	not null,
	name	varchar(255)	default null,
	title	varchar(255)	default null,
	description	varchar(255)	default null,
	link_url	varchar(255)	default null,
	alt_text	varchar(255)	default null,
	image_text_1	varchar(255)	default null,
	image_text_2	varchar(255)	default null,
	image_text_3	varchar(255)	default null,
	image_text_4	varchar(255)	default null
,constraint csa_media_content_xlate_p primary key (translation_id));


create table csa_media_media_xlate (
	id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
,constraint csa_media_media_xlate_p primary key (id,locale)
,constraint csa_media_media_xlate_f1 foreign key (translation_id) references csa_media_content_xlate (translation_id));

create index csa_media_media_xlate_i1 on csa_media_media_xlate (translation_id);
commit;


